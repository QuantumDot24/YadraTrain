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
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.complexsoft.yadratrain.data.EnginePreset
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
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun TrainingScreen(
    preset: EnginePreset,
    onTrainingFinished: (resultsString: String, correct: Int, total: Int, finalAccuracy: Float) -> Unit,
    viewModel: TrainingViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val totalEpochs = preset.epochs
    val listState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(preset) {
        val assets = (context as android.app.Activity).assets
        viewModel.start(preset, totalEpochs, assets)
    }

    LaunchedEffect(state.isFinished) {
        if (state.isFinished) {
            delay(200.milliseconds)
            val results = viewModel.inferClassifier(0, 10)
            val correct = results.count { it.pred == it.label }
            val total = results.size
            val resultsString = results.joinToString(";") { "${it.pred},${it.label}" }
            onTrainingFinished(resultsString, correct, total, state.accuracy)
        }
    }

    LaunchedEffect(state.logs.size) {
        if (state.logs.isNotEmpty()) listState.animateScrollToItem(state.logs.size - 1)
    }

    if (state.error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(YadraBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Error: ${state.error}",
                color = YadraError,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    if (state.isLoadingDataset) {
        DatasetLoadingScreen(preset = preset.displayName)
        return
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .padding(24.dp)
    ) {
        SectionLabel("step 2 of 3 · training")
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = preset.displayName,
            style = MaterialTheme.typography.headlineSmall,
            color = YadraTextPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "epoch ${state.epoch} / $totalEpochs",
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
            progress = { state.progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = YadraStructural,
            trackColor = YadraBorder,
            strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)
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
        LabeledDivider("latest events")
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
                containerColor = YadraError, disabledContainerColor = YadraSurface
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("cancel training", color = if (state.isTraining) YadraBg else YadraTextDim)
        }
    }
}

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
                color = YadraStructural, trackColor = YadraBorder, modifier = Modifier.height(36.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "loading dataset · $preset",
                style = MaterialTheme.typography.bodyMedium,
                color = YadraTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "reading .bin files and preparing GPU buffers...",
                style = MaterialTheme.typography.bodySmall,
                color = YadraTextDim
            )
        }
    }
}