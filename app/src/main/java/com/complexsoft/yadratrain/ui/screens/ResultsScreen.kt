package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.theme.YadraAccuracy
import com.complexsoft.yadratrain.ui.theme.YadraBg
import com.complexsoft.yadratrain.ui.theme.YadraBorder
import com.complexsoft.yadratrain.ui.theme.YadraError
import com.complexsoft.yadratrain.ui.theme.YadraStructural
import com.complexsoft.yadratrain.ui.theme.YadraSurface
import com.complexsoft.yadratrain.ui.theme.YadraTextDim
import com.complexsoft.yadratrain.ui.theme.YadraTextPrimary
import com.yadra.YadraEngine

@Composable
fun ResultsScreen(
    preset: String,
    resultsString: String,
    correct: Int,
    total: Int,
    finalAccuracy: Float,
    onNavigateToSummary: (correct: Int, total: Int, finalAccuracy: Float) -> Unit
) {
    val results = remember(resultsString) {
        resultsString.split(";").map { part ->
            val parts = part.split(",")
            YadraEngine.InferenceResult(parts[0].toInt(), parts[1].toInt())
        }
    }

    val classes = if (preset == "MNIST") {
        listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    } else {
        listOf("avion", "auto", "ave", "gato", "venado", "perro", "rana", "caballo", "barco", "camion")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .padding(24.dp)
    ) {
        SectionLabel("paso 3 de 3 · inferencia")
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Diagnóstico — $preset",
            style = MaterialTheme.typography.headlineSmall,
            color = YadraTextPrimary
        )
        Text(
            text = "primeras ${results.size} muestras del test set, predicción vs etiqueta real",
            style = MaterialTheme.typography.bodySmall,
            color = YadraTextDim
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 4.dp)
        ) {
            itemsIndexed(results) { index, result ->
                ResultCard(
                    index = index,
                    predLabel = classes.getOrElse(result.prediction) { "?" },
                    realLabel = classes.getOrElse(result.label) { "?" },
                    isCorrect = result.prediction == result.label
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onNavigateToSummary(correct, total, finalAccuracy) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = YadraStructural),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("ver resumen", color = YadraBg)
        }
    }
}

@Composable
private fun ResultCard(
    index: Int,
    predLabel: String,
    realLabel: String,
    isCorrect: Boolean
) {
    val accent = if (isCorrect) YadraAccuracy else YadraError

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(YadraSurface, RoundedCornerShape(12.dp))
            .border(1.dp, if (isCorrect) accent.copy(alpha = 0.4f) else accent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // header: índice de la muestra
        Text(
            text = "#%02d".format(index + 1),
            style = MaterialTheme.typography.labelSmall,
            color = YadraTextDim,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Start
        )

        // cuerpo: clase predicha grande, real chica debajo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = predLabel,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = YadraTextPrimary,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            if (!isCorrect) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "real: $realLabel",
                    style = MaterialTheme.typography.bodySmall,
                    color = YadraTextDim,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }

        // footer: palomita o tache
        Text(
            text = if (isCorrect) "✓" else "✗",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = accent
        )
    }
}