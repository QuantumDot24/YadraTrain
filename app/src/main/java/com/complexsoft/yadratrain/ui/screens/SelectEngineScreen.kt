package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.theme.YadraAccuracy
import com.complexsoft.yadratrain.ui.theme.YadraBg
import com.complexsoft.yadratrain.ui.theme.YadraBorder
import com.complexsoft.yadratrain.ui.theme.YadraLoss
import com.complexsoft.yadratrain.ui.theme.YadraStructural
import com.complexsoft.yadratrain.ui.theme.YadraSurface
import com.complexsoft.yadratrain.ui.theme.YadraTextDim
import com.complexsoft.yadratrain.ui.theme.YadraTextPrimary

@Composable
fun SelectEngineScreen(onEngineSelected: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .padding(24.dp)
    ) {
        SectionLabel("paso 1 de 3")
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Elige un motor",
            style = MaterialTheme.typography.headlineSmall,
            color = YadraTextPrimary
        )
        Text(
            text = "Cada uno corre 100% en GPU vía Vulkan, sin frameworks externos.",
            style = MaterialTheme.typography.bodySmall,
            color = YadraTextDim
        )

        Spacer(modifier = Modifier.height(28.dp))

        EngineSpecCard(
            name = "MLP · MNIST",
            arch = "784 → 128 → 64 → 10",
            optimizer = "Adam",
            accent = YadraStructural,
            onClick = { onEngineSelected("MNIST") }
        )
        Spacer(modifier = Modifier.height(14.dp))
        EngineSpecCard(
            name = "CNN · CIFAR-10",
            arch = "3 bloques conv+bn+pool",
            optimizer = "SGD + momentum",
            accent = YadraLoss,
            onClick = { onEngineSelected("CIFAR10") }
        )
    }
}

@Composable
private fun EngineSpecCard(
    name: String,
    arch: String,
    optimizer: String,
    accent: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(YadraSurface, RoundedCornerShape(12.dp))
            .border(1.dp, YadraBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                color = YadraTextPrimary
            )
            Text(
                text = "ENTRENAR  →",
                style = MaterialTheme.typography.labelSmall,
                color = accent
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        SpecRow("arquitectura", arch)
        SpecRow("optimizer", optimizer)
        SpecRow("backend", "Vulkan compute")
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
        Text(
            text = label.padEnd(13),
            style = MaterialTheme.typography.bodySmall,
            color = YadraTextDim
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = YadraTextPrimary
        )
    }
}