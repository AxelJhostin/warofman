package com.negocio.warofmen.componentes

import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.util.GameUtils

@Composable
fun WeightChart(history: List<String>) {
    val dataPoints = history.map { entry ->
        val parts = entry.split(":")
        val time = parts[0].toLongOrNull() ?: 0L
        val weight = parts[1].toFloatOrNull() ?: 0f
        Pair(time, weight)
    }.sortedBy { it.first }

    if (dataPoints.isEmpty()) {
        Text("No hay datos registrados aún.", color = Color.Gray, modifier = Modifier.padding(8.dp))
        return
    }

    val weights = dataPoints.map { it.second }
    val minWeight = (weights.minOrNull() ?: 0f) - 2f
    val maxWeight = (weights.maxOrNull() ?: 100f) + 2f

    Card(colors = CardDefaults.cardColors(containerColor = RpgPanel), modifier = Modifier.fillMaxWidth().height(200.dp).border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))) {
        Column(modifier = Modifier.padding(8.dp)) {
            Canvas(modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp)) {
                val width = size.width
                val height = size.height
                if (dataPoints.size == 1) {
                    drawLine(color = RpgNeonCyan, start = Offset(0f, height / 2), end = Offset(width, height / 2), strokeWidth = 5f)
                    return@Canvas
                }
                val path = Path()
                val xStep = width / (dataPoints.size - 1)
                dataPoints.forEachIndexed { index, point ->
                    val x = index * xStep
                    val normalizedY = (point.second - minWeight) / (maxWeight - minWeight)
                    val y = height - (normalizedY * height)
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    drawCircle(color = RpgNeonCyan, radius = 6f, center = Offset(x, y))
                }
                drawPath(path = path, color = RpgNeonCyan, style = Stroke(width = 4f))
                val fillPath = Path()
                fillPath.addPath(path)
                fillPath.lineTo(width, height)
                fillPath.lineTo(0f, height)
                fillPath.close()
                drawPath(path = fillPath, brush = Brush.verticalGradient(colors = listOf(RpgNeonCyan.copy(alpha = 0.3f), Color.Transparent)))
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(GameUtils.formatDate(dataPoints.first().first), color = Color.Gray, fontSize = 10.sp)
                Text(GameUtils.formatDate(dataPoints.last().first), color = Color.Gray, fontSize = 10.sp)
            }
        }
    }
}

@Composable
fun ExerciseChart(dataPoints: List<Pair<Long, Int>>) {
    val reps = dataPoints.map { it.second }
    val minReps = (reps.minOrNull() ?: 0) * 0.9f
    val maxReps = (reps.maxOrNull() ?: 100) * 1.1f

    Card(colors = CardDefaults.cardColors(containerColor = RpgPanel), modifier = Modifier.fillMaxWidth().height(250.dp).border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height
            if (dataPoints.size == 1) {
                drawLine(color = StatAgility, start = Offset(0f, height / 2), end = Offset(width, height / 2), strokeWidth = 5f)
                drawCircle(color = StatAgility, radius = 8f, center = Offset(width/2, height/2))
                return@Canvas
            }
            val path = Path()
            val xStep = width / (dataPoints.size - 1)
            dataPoints.forEachIndexed { index, point ->
                val x = index * xStep
                val normalizedY = (point.second - minReps) / (maxReps - minReps)
                val y = height - (normalizedY * height)
                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                drawCircle(color = StatAgility, radius = 6f, center = Offset(x, y))
            }
            drawPath(path = path, color = StatAgility, style = Stroke(width = 4f))
            val fillPath = Path()
            fillPath.addPath(path)
            fillPath.lineTo(width, height)
            fillPath.lineTo(0f, height)
            fillPath.close()
            drawPath(path = fillPath, brush = Brush.verticalGradient(colors = listOf(StatAgility.copy(alpha = 0.3f), Color.Transparent)))
        }
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(GameUtils.formatDate(dataPoints.first().first), color = Color.Gray, fontSize = 10.sp)
            Text(GameUtils.formatDate(dataPoints.last().first), color = Color.Gray, fontSize = 10.sp)
        }
    }
}

@Composable
fun StatBox(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}