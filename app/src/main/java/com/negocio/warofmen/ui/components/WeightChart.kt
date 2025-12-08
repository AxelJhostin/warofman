package com.negocio.warofmen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.ui.theme.RpgNeonCyan
import com.negocio.warofmen.ui.theme.RpgPanel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeightChart(
    logs: List<BodyLog>,
    modifier: Modifier = Modifier,
    lineColor: Color = RpgNeonCyan
) {
    if (logs.isEmpty()) return

    // Ordenamos por fecha (del más viejo al más nuevo)
    val sortedLogs = remember(logs) { logs.sortedBy { it.timestamp } }

    // Extraemos solo los valores de peso
    val weights = sortedLogs.map { it.weight }
    val maxWeight = weights.maxOrNull() ?: 100f
    val minWeight = weights.minOrNull() ?: 50f

    // Margen visual para que la gráfica no toque los bordes del canvas
    val yPadding = (maxWeight - minWeight) * 0.2f

    Column(modifier = modifier.fillMaxWidth()) {

        // El Lienzo donde dibujamos
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(RpgPanel)
                .padding(16.dp)
        ) {
            val width = size.width
            val height = size.height

            // Si solo hay un punto, dibujamos una línea recta al medio
            if (weights.size == 1) {
                drawLine(
                    color = lineColor,
                    start = Offset(0f, height / 2),
                    end = Offset(width, height / 2),
                    strokeWidth = 4.dp.toPx()
                )
                return@Canvas
            }

            // Escalas
            val xStep = width / (weights.size - 1)
            val yRange = (maxWeight + yPadding) - (minWeight - yPadding)

            // Path para la línea
            val path = Path()

            weights.forEachIndexed { index, weight ->
                val x = index * xStep
                // Invertimos Y porque en Canvas 0 está arriba
                val normalizedY = (weight - (minWeight - yPadding)) / yRange
                val y = height - (normalizedY * height)

                if (index == 0) {
                    path.moveTo(x, y.toFloat())
                } else {
                    path.lineTo(x, y.toFloat())
                }

                // Dibujar puntitos en cada registro
                drawCircle(
                    color = Color.White,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y.toFloat())
                )
            }

            // Dibujar la línea conectora
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx())
            )

            // Relleno degradado debajo de la línea (Efecto Pro)
            val fillPath = Path().apply {
                addPath(path)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
                )
            )
        }

        // Fechas en el eje X (Solo primera y última para no saturar)
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val fmt = SimpleDateFormat("dd MMM", Locale.getDefault())
            Text(fmt.format(Date(sortedLogs.first().timestamp)), color = Color.Gray, fontSize = 10.sp)
            Text(fmt.format(Date(sortedLogs.last().timestamp)), color = Color.Gray, fontSize = 10.sp)
        }
    }
}