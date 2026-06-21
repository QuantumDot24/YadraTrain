package com.complexsoft.yadratrain.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.complexsoft.yadratrain.ui.viewmodel.TrainingViewModel
import java.io.File

@Composable
fun SummaryScreen(
    preset: String,
    correct: Int,
    total: Int,
    finalAccuracy: Float,
    viewModel: TrainingViewModel = viewModel()
){
    val context = LocalContext.current
    val accuracyPercent = if (total > 0) (correct * 100f / total) else 0f

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        uri?.let {
            // Copiar el modelo a esa URI
            context.contentResolver.openOutputStream(it)?.use { outputStream ->
                // Guardar temporalmente y leer
                val tempFile = File(context.cacheDir, "model_export.yet")
                if (viewModel.saveModel(tempFile.absolutePath)) {
                    tempFile.inputStream().use { input ->
                        input.copyTo(outputStream)
                    }
                    tempFile.delete()
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "¡Entrenamiento completado!",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Correctas: $correct / $total",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text("Precisión global en test: %.1f%%".format(finalAccuracy * 100f), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = {
                exportLauncher.launch("${preset}_model.yet")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exportar como .yet")
        }
    }
}