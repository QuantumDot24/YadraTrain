package com.complexsoft.yadratrain.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.complexsoft.yadratrain.data.EnginePreset
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.theme.*
import com.complexsoft.yadratrain.ui.viewmodel.TrainingViewModel
import java.io.File

@Composable
fun SummaryScreen(
    preset: EnginePreset,
    correct: Int,
    total: Int,
    finalAccuracy: Float,
    viewModel: TrainingViewModel
) {
    val context = LocalContext.current
    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { out ->
                val tmp = File(context.cacheDir, "model_export.yet")
                if (viewModel.saveModel(tmp.absolutePath))
                    tmp.inputStream().use { inp -> inp.copyTo(out) }
                tmp.delete()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SectionLabel("training complete")
        Spacer(modifier = Modifier.height(12.dp))
        Text(preset.displayName,
            style = MaterialTheme.typography.headlineSmall, color = YadraTextPrimary)

        Spacer(modifier = Modifier.height(24.dp))

        if (preset.isClassifier) {
            StatCard(label = "CORRECT", value = "$correct / $total", accent = YadraAccuracy)
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                label = "TEST ACCURACY",
                value = "%.2f%%".format(finalAccuracy * 100f),
                accent = YadraStructural
            )
        } else {
            // Autoencoder — sin accuracy, solo confirmación
            StatCard(label = "SAMPLES RECONSTRUCTED", value = "$total", accent = YadraAccuracy)
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(label = "FINAL AVG LOSS (MSE)", value = "%.6f".format(finalAccuracy), accent = YadraLoss)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { exportLauncher.launch("${preset.name.lowercase()}_model.yet") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = YadraStructural),
            shape = RoundedCornerShape(10.dp)
        ) { Text("Export model (.yet)", color = YadraBg) }
    }
}

@Composable
private fun StatCard(label: String, value: String, accent: androidx.compose.ui.graphics.Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YadraSurface, RoundedCornerShape(12.dp))
            .border(1.dp, YadraBorder, RoundedCornerShape(12.dp))
            .padding(18.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = YadraTextDim)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value,
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = accent)
    }
}