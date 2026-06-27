package com.complexsoft.yadratrain.ui.viewmodel

import android.app.Application
import android.content.res.AssetManager
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.complexsoft.yadratrain.data.EnginePreset
import com.complexsoft.yadratrain.ui.components.LogEntry
import com.yadra.YadraTrainNative
import com.yadra.YadraTrainNative.AutoencoderResult
import com.yadra.YadraTrainNative.ClassifierSample
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

data class TrainingUiState(
    val epoch:            Int     = 0,
    val step:             Int     = 0,
    val loss:             Float   = 0f,
    val accuracy:         Float   = -1f,
    val isLoadingDataset: Boolean = true,
    val isTraining:       Boolean = false,
    val isFinished:       Boolean = false,
    val progress:         Float   = 0f,
    val logs:             List<LogEntry> = emptyList(),
    val error:            String? = null
)

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private var handle: Long = 0L
    private var preset: EnginePreset? = null
    private var numClasses: Int = 0
    private var totalEpochs: Int = 0
    private var trainingJob: Job? = null
    private var hasStarted = false
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US)

    private val _state = MutableStateFlow(TrainingUiState())
    val state: StateFlow<TrainingUiState> = _state

    fun start(preset: EnginePreset, epochs: Int, assets: AssetManager) {
        if (hasStarted) return
        hasStarted   = true
        this.preset  = preset
        totalEpochs  = epochs

        _state.update { it.copy(isLoadingDataset = true, isTraining = false, error = null) }

        trainingJob = viewModelScope.launch {
            try {
                // 1. Inicializar
                withContext(Dispatchers.IO) {
                    handle = YadraTrainNative.nativeInit(preset.ordinal_, assets)
                    Log.d("Yadra", "handle after init = $handle, preset=${preset.name}, ordinal=${preset.ordinal_}")
                }
                if (handle == 0L) {
                    _state.update { it.copy(isLoadingDataset = false, error = "nativeInit devolvió 0") }
                    return@launch
                }

                numClasses = YadraTrainNative.nativeNumClasses(handle)
                _state.update { it.copy(isLoadingDataset = false, isTraining = true) }

                // 2. Lanzar el poller justo antes del entrenamiento
                val pollerJob = launch {
                    var pollCount = 0
                    while (isActive) {
                        val raw = YadraTrainNative.nativePollMetric(handle)
                        if (raw != null) {
                            pushMetric(YadraTrainNative.unpackMetric(raw))
                        } else {
                            // Solo para debug: imprime cada 100 iteraciones (~1.6 s)
                            pollCount++
                            if (pollCount % 100 == 0) {
                                Log.d("YadraDebug", "Poller iteración $pollCount, raw sigue null")
                            }
                        }
                        delay(16)
                    }
                }
                // 3. Entrenar (bloquea hasta terminar)
                withContext(Dispatchers.IO) {
                    YadraTrainNative.nativeStartTraining(handle, epochs)
                }

                // 4. Drenar métricas residuales y detener poller
                drainMetrics(epochs)
                pollerJob.cancel()

                _state.update { it.copy(isTraining = false, isFinished = true) }

            } catch (e: Exception) {
                _state.update { it.copy(isLoadingDataset = false, isTraining = false, error = e.message) }
            }
        }
    }

    private fun drainMetrics(epochs: Int) {
        var raw = YadraTrainNative.nativePollMetric(handle)
        while (raw != null) {
            pushMetric(YadraTrainNative.unpackMetric(raw))
            raw = YadraTrainNative.nativePollMetric(handle)
        }
    }

    private fun pushMetric(m: YadraTrainNative.TrainMetric) {
        Log.d("YadraMetric", "epoch=${m.epoch} step=${m.step} loss=${m.loss} acc=${m.accuracy}")
        val isEpochSummary = m.step == -1
        _state.update { cur ->
            cur.copy(
                epoch    = m.epoch,
                step     = m.step,
                loss     = m.loss,
                accuracy = if (isEpochSummary) m.accuracy else cur.accuracy,
                progress = if (totalEpochs > 0) m.epoch.toFloat() / totalEpochs else 0f,
                logs     = cur.logs + LogEntry(
                    timeTag  = timeFmt.format(Date()),
                    epoch    = m.epoch,
                    step     = m.step,
                    loss     = m.loss,
                    accuracy = if (isEpochSummary) m.accuracy else null
                )
            )
        }
    }

    // ── Inferencia ────────────────────────────────────────────────────────

    fun inferClassifier(offset: Int, count: Int): List<ClassifierSample> {
        val raw = YadraTrainNative.nativeInferClassifier(handle, offset, count)
            ?: return emptyList()
        return YadraTrainNative.unpackClassifier(raw, numClasses)
    }

    fun inferAutoencoder(offset: Int, count: Int): AutoencoderResult? {
        val raw = YadraTrainNative.nativeInferAutoencoder(handle, offset, count)
            ?: return null
        return YadraTrainNative.unpackAutoencoder(raw, count)
    }

    fun numClasses(): Int = numClasses
    fun currentPreset(): EnginePreset? = preset

    fun stop() { if (handle != 0L) YadraTrainNative.nativeStopTraining(handle) }

    fun saveModel(path: String): Boolean =
        if (handle != 0L) YadraTrainNative.nativeSaveModel(handle, path) else false

    override fun onCleared() {
        super.onCleared()
        trainingJob?.cancel()
        if (handle != 0L) { YadraTrainNative.nativeDestroy(handle); handle = 0L }
    }
}