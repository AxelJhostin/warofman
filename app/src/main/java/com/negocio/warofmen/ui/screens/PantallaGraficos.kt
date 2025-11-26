package com.negocio.warofmen.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.components.ExerciseChart // Importado
import com.negocio.warofmen.ui.components.StatBox      // Importado
import com.negocio.warofmen.data.source.QuestProvider
import com.negocio.warofmen.ui.theme.*

@Composable
fun PantallaGraficos(
    workoutLogs: List<String>,
    level: Int,
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

            // Usamos el componente importado
            ExerciseChart(dataPoints = chartData)

            Spacer(modifier = Modifier.height(16.dp))

            // Resumen Rápido con componentes importados
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