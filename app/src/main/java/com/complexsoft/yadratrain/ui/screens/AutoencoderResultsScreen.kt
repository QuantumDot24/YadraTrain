package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.theme.*
import com.complexsoft.yadratrain.ui.viewmodel.TrainingViewModel
import com.yadra.YadraTrainNative.AutoencoderResult
import com.yadra.YadraTrainNative.AutoencoderSample

private const val SAMPLE_COUNT = 8

@Composable
fun AutoencoderResultsScreen(
    viewModel: TrainingViewModel,
    onNavigateToSummary: () -> Unit
) {
    val result = remember { viewModel.inferAutoencoder(0, SAMPLE_COUNT) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        SectionLabel("step 3 of 3 · reconstruction")
        Spacer(modifier = Modifier.height(6.dp))
        Text("Autoencoder Results",
            style = MaterialTheme.typography.headlineSmall, color = YadraTextPrimary)
        Text("Original vs reconstructed · Fashion-MNIST test samples",
            style = MaterialTheme.typography.bodySmall, color = YadraTextDim)

        Spacer(modifier = Modifier.height(16.dp))

        if (result == null) {
            Text("No results available.", color = YadraTextDim)
        } else {
            // MSE promedio
            MseHeader(mseAvg = result.mseAvg)

            Spacer(modifier = Modifier.height(20.dp))
            SectionLabel("original")
            Spacer(modifier = Modifier.height(8.dp))
            ImageRow(samples = result.samples, useOriginal = true)

            Spacer(modifier = Modifier.height(16.dp))
            SectionLabel("reconstructed")
            Spacer(modifier = Modifier.height(8.dp))
            ImageRow(samples = result.samples, useOriginal = false)

            Spacer(modifier = Modifier.height(20.dp))
            SectionLabel("per-sample mse")
            Spacer(modifier = Modifier.height(8.dp))
            MseGrid(samples = result.samples)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNavigateToSummary,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = YadraStructural),
            shape = RoundedCornerShape(10.dp)
        ) { Text("View summary", color = YadraBg) }
    }
}

@Composable
private fun MseHeader(mseAvg: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(YadraSurface, RoundedCornerShape(10.dp))
            .border(1.dp, YadraBorder, RoundedCornerShape(10.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("AVG RECONSTRUCTION MSE",
                style = MaterialTheme.typography.labelSmall, color = YadraTextDim)
            Text("%.6f".format(mseAvg),
                style = MaterialTheme.typography.titleLarge, color = YadraAccuracy)
        }
        Text("$SAMPLE_COUNT samples", style = MaterialTheme.typography.bodySmall, color = YadraTextDim)
    }
}

@Composable
private fun ImageRow(samples: List<AutoencoderSample>, useOriginal: Boolean) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        itemsIndexed(samples) { _, sample ->
            val pixels = if (useOriginal) sample.original else sample.reconstructed
            MnistCanvas(pixels = pixels, size = 80.dp)
        }
    }
}

@Composable
private fun MnistCanvas(pixels: FloatArray, size: androidx.compose.ui.unit.Dp) {
    Canvas(
        modifier = Modifier
            .size(size)
            .background(Color.Black, RoundedCornerShape(6.dp))
    ) {
        val cellW = this.size.width  / 28f
        val cellH = this.size.height / 28f
        for (row in 0 until 28) {
            for (col in 0 until 28) {
                val v = pixels[row * 28 + col].coerceIn(0f, 1f)
                drawRect(
                    color  = Color(v, v, v, 1f),
                    topLeft = Offset(col * cellW, row * cellH),
                    size   = Size(cellW, cellH)
                )
            }
        }
    }
}

@Composable
private fun MseGrid(samples: List<AutoencoderSample>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        samples.forEachIndexed { i, sample ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(YadraSurface, RoundedCornerShape(8.dp))
                    .border(1.dp, YadraBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("#%02d".format(i + 1),
                    style = MaterialTheme.typography.bodySmall, color = YadraTextDim)
                // Mini barra de MSE relativa al peor del lote
                val maxMse = samples.maxOf { it.mse }.coerceAtLeast(1e-6f)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                        .height(5.dp)
                        .background(YadraBorder, RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(sample.mse / maxMse)
                            .background(YadraLoss.copy(alpha = 0.7f), RoundedCornerShape(3.dp))
                    )
                }
                Text("%.6f".format(sample.mse),
                    style = MaterialTheme.typography.bodySmall, color = YadraLoss)
            }
        }
    }
}