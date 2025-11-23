package com.negocio.warofmen.vista

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.negocio.warofmen.dato.Quest
import com.negocio.warofmen.dato.QuestProvider
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.util.GameUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PantallaGraficos(
    workoutLogs: List<String>,
    level: Int, // Necesario para obtener los nombres de las misiones actuales
    onBack: () -> Unit
) {
    // Obtenemos las misiones posibles para llenar el selector
    val availableQuests = remember(level) { QuestProvider.getQuestsForLevel(level) }

    // Estado del selector
    var selectedQuest by remember { mutableStateOf(availableQuests.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    // Filtramos los logs para el ejercicio seleccionado
    val chartData = remember(selectedQuest, workoutLogs) {
        if (selectedQuest == null) emptyList()
        else {
            workoutLogs
                .map { it.split(":") }
                .filter { it.size == 3 && it[0].toInt() == selectedQuest!!.id } // Filtramos por ID
                .map {
                    val time = it[1].toLong()
                    val reps = it[2].toInt()
                    Pair(time, reps)
                }
                .sortedBy { it.first }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
                Text("🔙")
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("RENDIMIENTO", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // SELECTOR DE EJERCICIO (DROPDOWN)
        Text("SELECCIONA EJERCICIO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(RpgPanel, RoundedCornerShape(8.dp))
                .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedQuest?.title ?: "Seleccionar", color = Color.White, fontWeight = FontWeight.Bold)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = RpgNeonCyan)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(RpgPanel)
            ) {
                availableQuests.forEach { quest ->
                    DropdownMenuItem(
                        text = { Text(quest.title, color = if(quest == selectedQuest) RpgNeonCyan else Color.White) },
                        onClick = {
                            selectedQuest = quest
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // GRÁFICA DE RENDIMIENTO
        if (chartData.isNotEmpty()) {
            Text("PROGRESO (Volumen Total)", color = RpgNeonCyan, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            ExerciseChart(dataPoints = chartData)

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen Rápido
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                StatBox("MÁXIMO", "${chartData.maxOf { it.second }}")
                StatBox("TOTAL SESIONES", "${chartData.size}")
                StatBox("ÚLTIMO", "${chartData.last().second}")
            }

        } else {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay datos registrados para este ejercicio aún.", color = Color.Gray)
            }
        }
    }
}

@Composable
fun ExerciseChart(dataPoints: List<Pair<Long, Int>>) {
    val reps = dataPoints.map { it.second }
    val minReps = (reps.minOrNull() ?: 0) * 0.9f
    val maxReps = (reps.maxOrNull() ?: 100) * 1.1f // Margen superior

    Card(
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val width = size.width
            val height = size.height

            if (dataPoints.size == 1) {
                // Si solo hay un punto, dibujamos una línea horizontal
                drawLine(
                    color = StatAgility, // Verde Matrix
                    start = Offset(0f, height / 2),
                    end = Offset(width, height / 2),
                    strokeWidth = 5f
                )
                drawCircle(color = StatAgility, radius = 8f, center = Offset(width/2, height/2))
                return@Canvas
            }

            val path = Path()
            val xStep = width / (dataPoints.size - 1)

            dataPoints.forEachIndexed { index, point ->
                val x = index * xStep
                // Normalizamos Y
                val normalizedY = (point.second - minReps) / (maxReps - minReps)
                val y = height - (normalizedY * height)

                if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)

                drawCircle(color = StatAgility, radius = 6f, center = Offset(x, y))
            }

            // Dibujar línea
            drawPath(path = path, color = StatAgility, style = Stroke(width = 4f))

            // Relleno
            val fillPath = Path()
            fillPath.addPath(path)
            fillPath.lineTo(width, height)
            fillPath.lineTo(0f, height)
            fillPath.close()

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(StatAgility.copy(alpha = 0.3f), Color.Transparent)
                )
            )
        }

        // Fechas abajo
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
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