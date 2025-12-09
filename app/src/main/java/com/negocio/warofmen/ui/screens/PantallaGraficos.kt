package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.components.ExerciseChart
import com.negocio.warofmen.ui.components.StatBox
import com.negocio.warofmen.data.source.QuestProvider
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.data.model.WorkoutLog

@Composable
fun PantallaGraficos(
    workoutLogs: List<WorkoutLog>,
    level: Int,
    onBack: () -> Unit
) {
    val availableQuests = remember(level) { QuestProvider.getQuestsForLevel(level) }
    var selectedQuest by remember { mutableStateOf(availableQuests.firstOrNull()) }
    var expanded by remember { mutableStateOf(false) }

    val chartData = remember(selectedQuest, workoutLogs) {
        if (selectedQuest == null) emptyList()
        else {
            workoutLogs
                .filter { it.questId == selectedQuest!!.id } // Filtramos por ID numérico
                .map { log ->
                    Pair(log.timestamp, log.totalVolume) // Sacamos los datos directamente
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
            // SOLUCIÓN 2: Icono limpio en lugar de botón gris
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
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