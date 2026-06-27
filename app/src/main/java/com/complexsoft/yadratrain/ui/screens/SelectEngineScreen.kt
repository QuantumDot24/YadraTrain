package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.complexsoft.yadratrain.data.EnginePreset
import com.complexsoft.yadratrain.ui.components.SectionLabel
import com.complexsoft.yadratrain.ui.theme.*

@Composable
fun SelectEngineScreen(onEngineSelected: (EnginePreset) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(YadraBg)
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        SectionLabel("step 1 of 3")
        Spacer(modifier = Modifier.height(6.dp))
        Text("Select a model", style = MaterialTheme.typography.headlineSmall, color = YadraTextPrimary)
        Text(
            "All models train 100% on-device via Vulkan compute.",
            style = MaterialTheme.typography.bodySmall, color = YadraTextDim
        )
        Spacer(modifier = Modifier.height(28.dp))

        EngineCard(
            preset    = EnginePreset.MNIST,
            icon      = "🔢",
            arch      = "784 → 128 → 64 → 10",
            optimizer = "Adam  lr=1e-3",
            epochs    = "10 epochs",
            accent    = YadraStructural,
            onClick   = { onEngineSelected(EnginePreset.MNIST) }
        )
        Spacer(modifier = Modifier.height(14.dp))
        EngineCard(
            preset    = EnginePreset.FASHION_MNIST,
            icon      = "👗",
            arch      = "Conv32 → Conv64 → Pool → FC128 → FC10",
            optimizer = "Adam  lr=1e-3  cosine",
            epochs    = "5 epochs",
            accent    = YadraAccuracy,
            onClick   = { onEngineSelected(EnginePreset.FASHION_MNIST) }
        )
        Spacer(modifier = Modifier.height(14.dp))
        EngineCard(
            preset    = EnginePreset.CIFAR10,
            icon      = "🖼️",
            arch      = "LightFastNet · ResBlocks · GAP",
            optimizer = "Adam  lr=1e-3  cosine  warm-up",
            epochs    = "23 epochs",
            accent    = YadraLoss,
            onClick   = { onEngineSelected(EnginePreset.CIFAR10) }
        )
        Spacer(modifier = Modifier.height(14.dp))
        EngineCard(
            preset    = EnginePreset.CONV_AE,
            icon      = "🔁",
            arch      = "Encoder: Conv×3  Decoder: ConvT×3",
            optimizer = "Adam  lr=1e-3",
            epochs    = "10 epochs",
            accent    = YadraTextDim,
            onClick   = { onEngineSelected(EnginePreset.CONV_AE) }
        )
    }
}

@Composable
private fun EngineCard(
    preset: EnginePreset, icon: String, arch: String,
    optimizer: String, epochs: String, accent: Color, onClick: () -> Unit
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(icon, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.width(10.dp))
                Text(preset.displayName, style = MaterialTheme.typography.titleMedium, color = YadraTextPrimary)
            }
            Text("TRAIN  →", style = MaterialTheme.typography.labelSmall, color = accent)
        }
        Spacer(modifier = Modifier.height(10.dp))
        SpecRow("architecture", arch)
        SpecRow("optimizer",    optimizer)
        SpecRow("schedule",     epochs)
        SpecRow("backend",      "Vulkan compute")
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp)) {
        Text(label.padEnd(14), style = MaterialTheme.typography.bodySmall, color = YadraTextDim)
        Text(value,            style = MaterialTheme.typography.bodySmall, color = YadraTextPrimary)
    }
}