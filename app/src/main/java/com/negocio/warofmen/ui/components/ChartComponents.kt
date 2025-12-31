package com.negocio.warofmen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// --- GRÁFICA DE PESO (Mejorada con Grid y Glow) ---
@Composable
fun WeightChart(history: List<BodyLog>) {
    // Ordenar y preparar datos
    val sortedHistory = remember(history) { history.sortedBy { it.timestamp } }
    val dataPoints = sortedHistory.map { Pair(it.timestamp, it.weight) }

    if (dataPoints.isEmpty()) {
        EmptyChartBox("No hay registros de peso aún.")
        return
    }

    val weights = dataPoints.map { it.second }
    // Margen dinámico para que la gráfica respire
    val minWeight = (weights.minOrNull() ?: 0f) - 1f
    val maxWeight = (weights.maxOrNull() ?: 100f) + 1f

    ChartContainer(title = "HISTORIAL DE PESO") {
        LineChartCanvas(
            dataPoints = dataPoints,
            minY = minWeight,
            maxY = maxWeight,
            lineColor = RpgNeonCyan
        )
    }
}

// --- GRÁFICA DE EJERCICIO (Barras con Degradado) ---
@Composable
fun ExerciseChart(dataPoints: List<Pair<Long, Int>>) {
    if (dataPoints.isEmpty()) {
        EmptyChartBox("No hay datos para este ejercicio.")
        return
    }

    val maxVal = (dataPoints.maxOfOrNull { it.second } ?: 10).toFloat() * 1.2f

    ChartContainer(title = "RENDIMIENTO (REPS/TIEMPO)") {
        BarChartCanvas(
            dataPoints = dataPoints,
            maxY = maxVal,
            barColor = StatAgility // Verde Agilidad
        )
    }
}

// --- COMPONENTES VISUALES ---

@Composable
fun StatBox(
    label: String,
    value: String,
    color: Color = RpgNeonCyan,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(color.copy(alpha = 0.1f), Color.Transparent)
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label.uppercase(),
            color = Color.LightGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun EmptyChartBox(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(msg, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun ChartContainer(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .border(1.dp, Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header con puntito decorativo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).background(Color.Gray, RoundedCornerShape(50)))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

// --- CANVAS DE BARRAS MEJORADO ---
@Composable
private fun ColumnScope.BarChartCanvas(
    dataPoints: List<Pair<Long, Int>>,
    maxY: Float,
    barColor: Color
) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .padding(top = 16.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height
        val barCount = dataPoints.size

        // DIBUJAR LÍNEAS DE GRID (Fondo)
        val gridLines = 4
        val stepY = height / gridLines
        for (i in 0..gridLines) {
            val y = stepY * i
            drawLine(
                color = Color.DarkGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        // ESPACIADO INTELIGENTE
        val spacing = 12.dp.toPx()
        val calculatedBarWidth = (width - (spacing * (barCount - 1))) / barCount
        val barWidth = calculatedBarWidth.coerceAtMost(40.dp.toPx()) // Max ancho visual

        // Centrar
        val totalContentWidth = (barWidth * barCount) + (spacing * (barCount - 1))
        val startOffset = (width - totalContentWidth) / 2

        dataPoints.forEachIndexed { index, entry ->
            val value = entry.second.toFloat()
            val barHeight = (value / maxY) * height

            val x = startOffset + (index * (barWidth + spacing))
            val y = height - barHeight

            // Barra con degradado vertical
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(barColor, barColor.copy(alpha = 0.3f)),
                    startY = y,
                    endY = height
                ),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Borde brillante fino
            drawRoundRect(
                color = barColor.copy(alpha = 0.8f),
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx()),
                style = Stroke(width = 1.dp.toPx())
            )
        }
    }

    // Fechas abajo
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (dataPoints.isNotEmpty()) {
            Text(GameUtils.formatDate(dataPoints.first().first), color = Color.Gray, fontSize = 9.sp)
            Text(GameUtils.formatDate(dataPoints.last().first), color = Color.Gray, fontSize = 9.sp)
        }
    }
}

// --- CANVAS DE LÍNEA MEJORADO ---
@Composable
private fun ColumnScope.LineChartCanvas(
    dataPoints: List<Pair<Long, Float>>,
    minY: Float,
    maxY: Float,
    lineColor: Color
) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .height(180.dp)
        .padding(top = 16.dp, bottom = 8.dp)
    ) {
        val width = size.width
        val height = size.height

        // DIBUJAR GRID
        val gridLines = 3
        val stepY = height / gridLines
        for (i in 0..gridLines) {
            val y = stepY * i
            drawLine(
                color = Color.DarkGray.copy(alpha = 0.3f),
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
        }

        if (dataPoints.size == 1) {
            // Caso 1 punto: Línea recta al medio
            drawLine(
                color = lineColor,
                start = Offset(0f, height / 2),
                end = Offset(width, height / 2),
                strokeWidth = 3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f) // Punteada si es proyección
            )
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(width / 2, height / 2))
            return@Canvas
        }

        val path = Path()
        val xStep = width / (dataPoints.size - 1)

        dataPoints.forEachIndexed { index, point ->
            val x = index * xStep
            val normalizedY = (point.second - minY) / (maxY - minY)
            val y = height - (normalizedY * height)

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        // 1. Sombra de la línea (Glow)
        drawPath(
            path = path,
            color = lineColor.copy(alpha = 0.4f),
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round) // Más grueso y transparente
        )

        // 2. Línea principal nítida
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )

        // 3. Relleno Degradado
        val fillPath = Path().apply {
            addPath(path)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.2f), Color.Transparent),
                startY = 0f,
                endY = height
            )
        )

        // 4. Puntos (Círculos)
        dataPoints.forEachIndexed { index, point ->
            val x = index * xStep
            val normalizedY = (point.second - minY) / (maxY - minY)
            val y = height - (normalizedY * height)

            // Círculo negro interior (para tapar la línea)
            drawCircle(color = RpgPanel, radius = 4.dp.toPx(), center = Offset(x, y))
            // Borde de color
            drawCircle(color = lineColor, radius = 4.dp.toPx(), center = Offset(x, y), style = Stroke(width = 1.5.dp.toPx()))
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (dataPoints.isNotEmpty()) {
            val fmt = SimpleDateFormat("dd MMM", Locale.getDefault())
            Text(fmt.format(Date(dataPoints.first().first)), color = Color.Gray, fontSize = 9.sp)
            Text(fmt.format(Date(dataPoints.last().first)), color = Color.Gray, fontSize = 9.sp)
        }
    }
}