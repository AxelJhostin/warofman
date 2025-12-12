package com.negocio.warofmen.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.ui.theme.*

// --- GRÁFICA DE PESO (Sin cambios mayores, solo estilos) ---
@Composable
fun WeightChart(history: List<BodyLog>) {
    val sortedHistory = history.sortedBy { it.timestamp }
    val dataPoints = sortedHistory.map { Pair(it.timestamp, it.weight) }

    if (dataPoints.isEmpty()) {
        EmptyChartBox("No hay registros de peso aún.")
        return
    }

    val weights = dataPoints.map { it.second }
    val minWeight = (weights.minOrNull() ?: 0f) - 2f
    val maxWeight = (weights.maxOrNull() ?: 100f) + 2f

    ChartContainer {
        ChartCanvas(dataPoints = dataPoints, minY = minWeight, maxY = maxWeight, lineColor = RpgNeonCyan)
    }
}

// --- GRÁFICA DE EJERCICIO (Mejorada) ---
@Composable
fun ExerciseChart(dataPoints: List<Pair<Long, Int>>) {
    if (dataPoints.isEmpty()) {
        EmptyChartBox("No hay datos para este ejercicio.")
        return
    }

    val values = dataPoints.map { it.second.toFloat() }
    // Ajuste dinámico: Si todos son iguales (ej: 24, 24), damos margen visual
    val minVal = (values.minOrNull() ?: 0f) * 0.9f
    val maxVal = (values.maxOrNull() ?: 100f) * 1.1f
    // Si min y max son iguales, forzamos una diferencia para que la línea no quede pegada
    val safeMax = if (minVal == maxVal) maxVal + 10f else maxVal

    ChartContainer {
        ChartCanvas(
            dataPoints = dataPoints.map { it.first to it.second.toFloat() },
            minY = minVal,
            maxY = safeMax,
            lineColor = StatAgility
        )
    }
}

// --- CAJITA DE ESTADÍSTICAS (EL CAMBIO VISUAL IMPORTANTE) ---
@Composable
fun StatBox(
    label: String,
    value: String,
    color: Color = RpgNeonCyan,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(4.dp) // Espacio entre cajitas
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .background(RpgPanel.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label.uppercase(),
            color = Color.Gray,
            fontSize = 10.sp, // Letra pequeña para etiquetas largas
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis // "..." si es muy largo
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = color,
            fontSize = 20.sp, // Número grande
            fontWeight = FontWeight.ExtraBold
        )
    }
}

// --- COMPONENTES INTERNOS REUTILIZABLES (Para limpiar código) ---

@Composable
private fun EmptyChartBox(msg: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Text(msg, color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
private fun ChartContainer(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            content()
        }
    }
}

@Composable
private fun ColumnScope.ChartCanvas(
    dataPoints: List<Pair<Long, Float>>,
    minY: Float,
    maxY: Float,
    lineColor: Color
) {
    Canvas(modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        val width = size.width
        val height = size.height

        if (dataPoints.size == 1) {
            // Caso: Un solo punto (Dibujar línea recta al medio)
            drawLine(
                color = lineColor,
                start = Offset(0f, height / 2),
                end = Offset(width, height / 2),
                strokeWidth = 5f
            )
            drawCircle(color = lineColor, radius = 6f, center = Offset(width / 2, height / 2))
            return@Canvas
        }

        val path = Path()
        val xStep = width / (dataPoints.size - 1)

        dataPoints.forEachIndexed { index, point ->
            val x = index * xStep
            // Normalización para que encaje en el alto
            val normalizedY = (point.second - minY) / (maxY - minY)
            val y = height - (normalizedY * height)

            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
            drawCircle(color = lineColor, radius = 5f, center = Offset(x, y))
        }

        // Dibujar línea
        drawPath(path = path, color = lineColor, style = Stroke(width = 4f))

        // Dibujar degradado debajo (Sombreado)
        val fillPath = Path()
        fillPath.addPath(path)
        fillPath.lineTo(width, height)
        fillPath.lineTo(0f, height)
        fillPath.close()

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.3f), Color.Transparent)
            )
        )
    }

    // Fechas abajo
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(GameUtils.formatDate(dataPoints.first().first), color = Color.Gray, fontSize = 9.sp)
        Text(GameUtils.formatDate(dataPoints.last().first), color = Color.Gray, fontSize = 9.sp)
    }
}