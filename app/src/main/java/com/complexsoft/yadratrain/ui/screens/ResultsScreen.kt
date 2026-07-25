package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.complexsoft.yadratrain.data.EnginePreset
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.theme.*
import com.complexsoft.yadratrain.ui.viewmodel.TrainingViewModel
import com.yadra.YadraTrainNative.ClassifierSample

// ── Preset Mappings ───────────────────────────────────────────────────────

private val MNIST_ICONS = listOf("0️⃣","1️⃣","2️⃣","3️⃣","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣")

private val FASHION_ICONS  = listOf("👕","👖","🧥","👗","🧣","👡","👔","👟","👜","👢")
private val FASHION_LABELS = listOf("T-shirt","Trouser","Pullover","Dress","Coat",
    "Sandal","Shirt","Sneaker","Bag","Ankle boot")

private val CIFAR_ICONS  = listOf("✈️","🚗","🐦","🐱","🦌","🐶","🐸","🐴","🚢","🚚")
private val CIFAR_LABELS = listOf("Airplane","Automobile","Bird","Cat","Deer",
    "Dog","Frog","Horse","Ship","Truck")

@Composable
fun ResultsScreen(
    preset: EnginePreset,
    viewModel: TrainingViewModel,
    onNavigateToSummary: (correct: Int, total: Int, finalAccuracy: Float) -> Unit
) {
    val count = 10
    val samples = remember { viewModel.inferClassifier(0, count) }
    val correct = remember(samples) { samples.count { it.pred == it.label } }

    val icons  = when (preset) {
        EnginePreset.MNIST         -> MNIST_ICONS
        EnginePreset.FASHION_MNIST -> FASHION_ICONS
        EnginePreset.CIFAR10       -> CIFAR_ICONS
        else                       -> MNIST_ICONS
    }
    val labels = when (preset) {
        EnginePreset.FASHION_MNIST -> FASHION_LABELS
        EnginePreset.CIFAR10       -> CIFAR_LABELS
        else                       -> MNIST_ICONS
    }

    Column(
        modifier = Modifier.fillMaxSize().background(YadraBg).padding(24.dp)
    ) {
        SectionLabel("step 3 of 3 · inference")
        Spacer(modifier = Modifier.height(6.dp))
        Text("Results — ${preset.displayName}",
            style = MaterialTheme.typography.headlineSmall, color = YadraTextPrimary)
        Text("First $count samples from test set · prediction vs ground truth",
            style = MaterialTheme.typography.bodySmall, color = YadraTextDim)

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            itemsIndexed(samples) { index, sample ->
                ClassifierCard(
                    index     = index,
                    sample    = sample,
                    icons     = icons,
                    labels    = labels,
                    numClasses = viewModel.numClasses()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                onNavigateToSummary(correct, count, viewModel.state.value.accuracy)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = YadraStructural),
            shape = RoundedCornerShape(10.dp)
        ) { Text("View summary", color = YadraBg) }
    }
}

@Composable
private fun ClassifierCard(
    index: Int, sample: ClassifierSample,
    icons: List<String>, labels: List<String>, numClasses: Int
) {
    val isCorrect = sample.pred == sample.label
    val accent    = if (isCorrect) YadraAccuracy else YadraError
    val predIcon  = icons.getOrElse(sample.pred)  { "?" }
    val predLabel = labels.getOrElse(sample.pred)  { "?" }
    val realLabel = labels.getOrElse(sample.label) { "?" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YadraSurface, RoundedCornerShape(12.dp))
            .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text("#%02d".format(index + 1),
            style = MaterialTheme.typography.labelSmall,
            color = YadraTextDim,
            modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(6.dp))

        // Icono grande
        Text(predIcon, fontSize = 32.sp, textAlign = TextAlign.Center)

        // Prediction label
        Text(predLabel,
            style = MaterialTheme.typography.bodySmall,
            color = YadraTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1)

        // Ground truth only if it failed
        if (!isCorrect) {
            Text("real: $realLabel",
                style = MaterialTheme.typography.labelSmall,
                color = YadraTextDim,
                textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Probability bars — top-3
        ProbBars(probs = sample.probs, labels = labels, accent = accent)

        Spacer(modifier = Modifier.height(6.dp))

        // Checkmark / X
        Text(if (isCorrect) "✓" else "✗", fontSize = 18.sp, color = accent)
    }
}

@Composable
private fun ProbBars(probs: FloatArray, labels: List<String>, accent: Color) {
    // Top-3 by probability
    val top3 = probs.indices
        .sortedByDescending { probs[it] }
        .take(3)

    Column(modifier = Modifier.fillMaxWidth()) {
        top3.forEach { classIdx ->
            val prob  = probs[classIdx]
            val label = labels.getOrElse(classIdx) { classIdx.toString() }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label.take(7),
                    style = MaterialTheme.typography.labelSmall,
                    color = YadraTextDim,
                    modifier = Modifier.width(52.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(YadraBorder, RoundedCornerShape(3.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(prob)
                            .background(accent.copy(alpha = 0.8f), RoundedCornerShape(3.dp))
                    )
                }
                Text(
                    text = "%.0f%%".format(prob * 100f),
                    style = MaterialTheme.typography.labelSmall,
                    color = YadraTextDim,
                    modifier = Modifier.width(32.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}