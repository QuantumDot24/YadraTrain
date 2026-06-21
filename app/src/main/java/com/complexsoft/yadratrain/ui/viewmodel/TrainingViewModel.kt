package com.complexsoft.yadratrain.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.complexsoft.yadratrain.ui.components.LogEntry
import com.yadra.EnginePreset
import com.yadra.YadraEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class TrainingUiState(
    val epoch: Int = 0,
    val step: Int = 0,
    val loss: Float = 0f,
    val accuracy: Float = -1f,
    val isLoadingDataset: Boolean = false,
    val isTraining: Boolean = false,
    val isFinished: Boolean = false,
    val progress: Float = 0f,
    val logs: List<LogEntry> = emptyList(),
    val error: String? = null
)

class TrainingViewModel(application: Application) : AndroidViewModel(application) {

    private var engine: YadraEngine? = null
    private var trainingJob: Job? = null
    private var currentPreset: EnginePreset? = null
    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.US)

    // Bandera interna para evitar relanzar start() dos veces (ej. recomposición
    // o reentrada del LaunchedEffect). Separada de isLoadingDataset a propósito:
    // isLoadingDataset es del estado inicial de la UI (nace en true para que el
    // primer frame ya muestre el loading), no debe usarse como lock de start().
    private var hasStarted = false

    private val _state = MutableStateFlow(TrainingUiState(isLoadingDataset = true))
    val state: StateFlow<TrainingUiState> = _state

    fun start(preset: EnginePreset, totalEpochs: Int) {
        if (hasStarted) return
        hasStarted = true
        currentPreset = preset
        _state.update {
            it.copy(isLoadingDataset = true, isTraining = false, isFinished = false, error = null)
        }

        trainingJob = viewModelScope.launch {
            // El engine es JNI + Vulkan propio: NO movemos su construcción a
            // Dispatchers.IO, porque si el código nativo asume un hilo fijo
            // para crear el VkDevice/queues o cachea el JNIEnv*, cambiar de
            // hilo acá podría romper el estado de Vulkan o producir UB.
            // Se mantiene en el dispatcher de siempre (Main, vía viewModelScope).
            //
            // El "frame que se pinta a medias" se resuelve del lado de
            // TrainingScreen con withFrameNanos (requiere el frame clock de
            // composición, no disponible aquí). El LaunchedEffect allá espera
            // a que el frame de loading se pinte de verdad ANTES de llamar a
            // este start(), así el usuario ya ve el loading completo antes de
            // que esto bloquee el hilo principal con la llamada nativa.
            val app = getApplication<Application>()
            engine = YadraEngine(app, preset)

            _state.update { it.copy(isLoadingDataset = false, isTraining = true) }

            engine?.train(totalEpochs, viewModelScope)?.collect { metric ->
                // metric.step == -1 marca el resumen de fin de época, que es el
                // único punto donde el accuracy es un dato real (evaluado sobre
                // el set completo). En steps intermedios el motor no calcula
                // accuracy, así que NO debemos pisar el valor anterior con lo
                // que venga ahí — de lo contrario el cuadro grande "parpadea":
                // aparece en el resumen y se borra en el primer step de la
                // siguiente época.
                val isEpochSummary = metric.step == -1

                _state.update { current ->
                    current.copy(
                        epoch = metric.epoch,
                        step = metric.step,
                        loss = metric.loss,
                        accuracy = if (isEpochSummary) metric.accuracy else current.accuracy,
                        progress = if (totalEpochs > 0) metric.epoch.toFloat() / totalEpochs else 0f,
                        logs = current.logs + toLogEntry(metric)
                    )
                }
            }
            _state.update { it.copy(isTraining = false, isFinished = true) }
        }
    }

    fun inferBatch(offset: Int, count: Int): List<YadraEngine.InferenceResult> {
        return engine?.inferBatch(offset, count) ?: emptyList()
    }

    fun numClasses(): Int = engine?.numClasses() ?: 10

    fun stop() {
        engine?.stop()
        trainingJob?.cancel()
    }

    fun saveModel(path: String): Boolean {
        return engine?.saveModel(path) ?: false
    }

    override fun onCleared() {
        super.onCleared()
        stop()
        engine?.destroy()
        engine = null
    }

    private fun toLogEntry(metric: com.yadra.TrainMetric): LogEntry = LogEntry(
        timeTag = timeFmt.format(Date()),
        epoch = metric.epoch,
        step = metric.step,
        loss = metric.loss,
        accuracy = if (metric.step == -1) metric.accuracy else null
    )
}