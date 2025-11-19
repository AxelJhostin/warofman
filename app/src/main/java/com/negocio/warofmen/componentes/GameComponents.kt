package com.negocio.warofmen.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.dato.Quest

// 1. Barra de Experiencia (XP) estilo RPG
@Composable
fun XpProgressBar(currentXp: Int, maxXp: Int, level: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Nivel $level", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "$currentXp / $maxXp XP", style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { currentXp.toFloat() / maxXp.toFloat() },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = Color(0xFF4CAF50), // Verde RPG
            trackColor = Color.LightGray,
        )
    }
}

// 2. Tarjeta de Misión (Quest Card)
@Composable
fun QuestCard(quest: Quest, onComplete: () -> Unit) {
    // Detectamos si es una misión de tiempo buscando la palabra "segundos" en la descripción
    // (Esto es un hack rápido para el MVP, idealmente tendríamos un campo 'type' en la clase Quest)
    val isTimeQuest = quest.description.contains("segundos")

    // Estado del temporizador
    var timeLeft by remember { mutableStateOf(if (isTimeQuest) extractSeconds(quest.description) else 0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Lógica del cronómetro
    LaunchedEffect(key1 = isTimerRunning, key2 = timeLeft) {
        if (isTimerRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        } else if (isTimerRunning && timeLeft == 0) {
            isTimerRunning = false
            onComplete() // Auto-completar cuando termina el tiempo
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = quest.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Badge(containerColor = Color(0xFFFFC107)) {
                    Text(text = "+${quest.xpReward} XP", color = Color.Black, modifier = Modifier.padding(4.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = quest.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Bonus: ${quest.statBonus}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )

                if (isTimeQuest) {
                    Button(
                        onClick = { isTimerRunning = !isTimerRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTimerRunning) Color.Red else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(if (timeLeft == 0) "¡Hecho!" else if (isTimerRunning) "$timeLeft s" else "Iniciar Crono")
                    }
                } else {
                    Button(onClick = onComplete) {
                        Text("Completar")
                    }
                }
            }
        }
    }
}

// Función auxiliar pequeña para sacar el número de la descripción (ej: "Aguanta 10 segundos" -> 10)
fun extractSeconds(description: String): Int {
    val number = description.filter { it.isDigit() }
    return if (number.isNotEmpty()) number.toInt() else 10
}

@Composable
fun LevelUpDialog(level: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "¡SUBIDA DE NIVEL!",
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFE91E63), // Un color rosado/rojo intenso
                fontSize = 24.sp
            )
        },
        text = {
            Column {
                Text("¡Felicidades! Has alcanzado el Nivel $level.")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Tus estadísticas han aumentado.")
                Text("¡Los enemigos (ejercicios) ahora son más fuertes!")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("¡A SEGUIR!")
            }
        },
        icon = {
            // Usamos un texto grande como icono por simplicidad
            Text("⭐", fontSize = 40.sp)
        }
    )
}