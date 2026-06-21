package com.complexsoft.yadratrain.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.complexsoft.yadratrain.ui.components.BlinkingCursor
import com.complexsoft.yadratrain.ui.theme.YadraAccuracy
import com.complexsoft.yadratrain.ui.theme.YadraBg
import com.complexsoft.yadratrain.ui.theme.YadraStructural
import com.complexsoft.yadratrain.ui.theme.YadraTextDim
import com.complexsoft.yadratrain.ui.theme.YadraTextPrimary
import kotlinx.coroutines.delay

private val bootLines = listOf(
    "searching for Vulkan layers...",
    "extension VK_KHR_shader_float16_int8 enabled",
    "extension VK_KHR_16bit_storage enabled",
    "device: Mali-G615 MC6",
    "yadra core initialized"
)

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {
    val visibleLines = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        for (line in bootLines) {
            delay(260)
            visibleLines.add(line)
        }
        delay(450)
        onSplashFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize().background(YadraBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "YADRA",
                style = MaterialTheme.typography.displayLarge,
                color = YadraTextPrimary
            )
            Text(
                text = "vulkan-native on-device training",
                style = MaterialTheme.typography.labelSmall,
                color = YadraStructural
            )
            Spacer(modifier = Modifier.height(28.dp))

            visibleLines.forEachIndexed { index, line ->
                val isLast = index == bootLines.lastIndex
                Row {
                    Text(
                        text = if (isLast) "[ ok ] " else "[ .. ] ",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isLast) YadraAccuracy else YadraTextDim
                    )
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = YadraTextDim
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }

            if (visibleLines.size < bootLines.size) {
                Row {
                    Text("[ .. ] ", style = MaterialTheme.typography.bodySmall, color = YadraTextDim)
                    BlinkingCursor(modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}