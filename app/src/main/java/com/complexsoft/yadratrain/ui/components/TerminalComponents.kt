package com.complexsoft.yadratrain.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import com.complexsoft.yadratrain.ui.theme.YadraAccuracy
import com.complexsoft.yadratrain.ui.theme.YadraBorder
import com.complexsoft.yadratrain.ui.theme.YadraLoss
import com.complexsoft.yadratrain.ui.theme.YadraStructural
import com.complexsoft.yadratrain.ui.theme.YadraSurface
import com.complexsoft.yadratrain.ui.theme.YadraTextDim
import com.complexsoft.yadratrain.ui.theme.YadraTextFaint
import com.complexsoft.yadratrain.ui.theme.YadraTextPrimary

/**
 * Una línea del log con tokens coloreados — el elemento firma de la app.
 * Convierte "Época 2, step 14: loss=2.1093" en algo que se lee como un
 * terminal real, con cada dato en su propio color: época (cian),
 * loss (naranja), accuracy (verde).
 */
@Composable
fun TerminalLogLine(entry: LogEntry, modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        withStyle(SpanStyle(color = YadraTextFaint)) { append("${entry.timeTag} ") }
        withStyle(SpanStyle(color = YadraStructural)) { append("época ${entry.epoch}") }
        if (entry.step >= 0) {
            withStyle(SpanStyle(color = YadraTextDim)) { append(" · step ${entry.step}") }
        } else {
            withStyle(SpanStyle(color = YadraAccuracy)) { append(" · resumen") }
        }
        withStyle(SpanStyle(color = YadraTextFaint)) { append("  ") }
        withStyle(SpanStyle(color = YadraTextDim)) { append("loss=") }
        withStyle(SpanStyle(color = YadraLoss)) { append("%.4f".format(entry.loss)) }
        if (entry.accuracy != null) {
            withStyle(SpanStyle(color = YadraTextDim)) { append("  acc=") }
            withStyle(SpanStyle(color = YadraAccuracy)) { append("%.2f%%".format(entry.accuracy * 100f)) }
        }
    }
    Text(text = text, style = MaterialTheme.typography.bodySmall, modifier = modifier)
}

data class LogEntry(
    val timeTag: String,
    val epoch: Int,
    val step: Int,
    val loss: Float,
    val accuracy: Float?
)

/** Cursor de bloque parpadeante, como un terminal a la espera de input. */
@Composable
fun BlinkingCursor(color: Color = YadraStructural, modifier: Modifier = Modifier) {
    val alpha by rememberInfiniteTransition(label = "cursor").animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(550), RepeatMode.Reverse),
        label = "cursorAlpha"
    )
    Box(
        modifier = modifier
            .size(width = 9.dp, height = 16.dp)
            .alpha(alpha)
            .background(color)
    )
}

/** Etiqueta pequeña tipo "eyebrow" — metadata estructural, no decoración. */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = YadraTextDim,
        modifier = modifier
    )
}

/** Tarjeta de métrica grande (usada para loss/accuracy en TrainingScreen). */
@Composable
fun MetricCard(label: String, value: String, accent: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(YadraSurface, RoundedCornerShape(10.dp))
            .border(1.dp, YadraBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        SectionLabel(label)
        Spacer(modifier = Modifier.padding(top = 2.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = accent
        )
    }
}

/** Divisor con etiqueta, ej. "── ÚLTIMOS EVENTOS ──". Encierra info real, no decora. */
@Composable
fun LabeledDivider(label: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.width(14.dp).size(1.dp).background(YadraBorder))
        Text(
            text = "  ${label.uppercase()}  ",
            style = MaterialTheme.typography.labelSmall,
            color = YadraTextDim
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 0.dp)
                .size(1.dp)
                .background(YadraBorder)
        )
    }
}