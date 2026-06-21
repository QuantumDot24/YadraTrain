package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.complexsoft.yadratrain.ui.components.LabeledDivider
import com.complexsoft.yadratrain.ui.components.MetricCard
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.components.TerminalLogLine
import com.complexsoft.yadratrain.ui.theme.YadraAccuracy
import com.complexsoft.yadratrain.ui.theme.YadraBg
import com.complexsoft.yadratrain.ui.theme.YadraBorder
import com.complexsoft.yadratrain.ui.theme.YadraError
import com.complexsoft.yadratrain.ui.theme.YadraLoss
import com.complexsoft.yadratrain.ui.theme.YadraStructural
import com.complexsoft.yadratrain.ui.theme.YadraSurface
import com.complexsoft.yadratrain.ui.theme.YadraTextDim
import com.complexsoft.yadratrain.ui.theme.YadraTextPrimary
import com.complexsoft.yadratrain.ui.viewmodel.TrainingViewModel
import com.yadra.EnginePreset
import kotlinx.coroutines.delay

@Composable
fun TrainingScreen(
    preset: String,
    onTrainingFinished: (resultsString: String, correct: Int, total: Int, finalAccuracy: Float) -> Unit,
    viewModel: TrainingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val totalEpochs = 10
    val listState = rememberLazyListState()

    LaunchedEffect(preset) {
        val enginePreset = when (preset) {
            "MNIST" -> EnginePreset.MLP_MNIST
            "CIFAR10" -> EnginePreset.CNN_CIFAR10
            else -> throw IllegalArgumentException("Preset desconocido")
        }
        // El ViewModel ya nace con isLoadingDataset=true (ver TrainingViewModel),
        // así que el primer frame compuesto de TrainingScreen ya debería pintar
        // el loading correctamente. Igual esperamos a que ese frame se complete
        // de verdad antes de llamar a start() (que bloquea el hilo con JNI),
        // porque yield() no garantiza un frame real — solo cede el turno lógico.
        androidx.compose.runtime.withFrameNanos { }
        viewModel.start(enginePreset, totalEpochs)
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            delay(200)
            val results = viewModel.inferBatch(0, 10)
            val correct = results.count { it.prediction == it.label }
            val total = results.size
            val resultsString = results.joinToString(";") { "${it.prediction},${it.label}" }
            onTrainingFinished(resultsString, correct, total, state.accuracy)
        }
    }

    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) listState.animateScrollToItem(state.logs.size - 1)
    }

    if (state.isLoadingDataset) {
        DatasetLoadingScreen(preset = preset)
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .padding(24.dp)
    ) {
        SectionLabel("paso 2 de 3 · entrenando")
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = preset,
            style = MaterialTheme.typography.headlineSmall,
            color = YadraTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "época ${state.epoch} / $totalEpochs",
                style = MaterialTheme.typography.bodyMedium,
                color = YadraStructural
            )
            Text(
                text = "%.0f%%".format(state.progress * 100f),
                style = MaterialTheme.typography.bodyMedium,
                color = YadraTextDim
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = state.progress,
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = YadraStructural,
            trackColor = YadraBorder
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                label = "loss",
                value = "%.4f".format(state.loss),
                accent = YadraLoss,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                label = "accuracy",
                value = if (state.accuracy >= 0f) "%.2f%%".format(state.accuracy * 100f) else "—",
                accent = YadraAccuracy,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        LabeledDivider("últimos eventos")
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(YadraSurface, RoundedCornerShape(10.dp))
                .border(1.dp, YadraBorder, RoundedCornerShape(10.dp))
                .padding(12.dp)
        ) {
            LazyColumn(state = listState) {
                items(state.logs) { entry ->
                    TerminalLogLine(entry = entry, modifier = Modifier.padding(vertical = 1.5.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.stop() },
            enabled = state.isTraining,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = YadraError,
                disabledContainerColor = YadraSurface
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("cancelar entrenamiento", color = if (state.isTraining) YadraBg else YadraTextDim)
        }
    }
}

/**
 * Pantalla puente mientras se cargan los datos (ej. los .bin de CIFAR-10).
 * Mantiene el lenguaje visual "terminal" de la app en vez de un spinner genérico,
 * y deja claro al usuario que su tap sí funcionó — solo está cargando el dataset.
 */
@Composable
private fun DatasetLoadingScreen(preset: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = YadraStructural,
                trackColor = YadraBorder,
                modifier = Modifier.height(36.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "cargando dataset · $preset",
                style = MaterialTheme.typography.bodyMedium,
                color = YadraTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "leyendo archivos .bin y preparando buffers en GPU...",
                style = MaterialTheme.typography.bodySmall,
                color = YadraTextDim
            )
        }
    }
}