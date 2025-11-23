package com.negocio.warofmen.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.dato.Quest
import com.negocio.warofmen.ui.theme.* // Importamos tus colores RPG
import com.negocio.warofmen.util.GameUtils

// 1. Barra de Experiencia (XP) estilo HUD
@Composable
fun XpProgressBar(currentXp: Int, maxXp: Int, level: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("LVL $level", fontWeight = FontWeight.ExtraBold, color = RpgNeonCyan, fontSize = 16.sp)
            Text("$currentXp / $maxXp XP", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(6.dp))

        // Barra con efecto visual
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .clip(CutCornerShape(bottomEnd = 8.dp))
                .background(Color.Black)
                .border(1.dp, Color.DarkGray, CutCornerShape(bottomEnd = 8.dp))
        ) {
            val progress = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(XpBarGreen.copy(alpha = 0.6f), XpBarGreen)
                        )
                    )
            )
        }
    }
}

// 2. Tarjeta de Misión (Quest Card) Estilo RPG
@Composable
fun QuestCard(quest: Quest, onComplete: () -> Unit) {
    val isTimeQuest = quest.description.contains("segundos") || quest.description.contains("Wall-sit")
    var timeLeft by remember { mutableStateOf(if (isTimeQuest) GameUtils.extractSeconds(quest.description) else 0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Determinar color según el Stat que mejora
    val questColor = when(quest.statBonus) {
        "STR" -> StatStrength
        "AGI" -> StatAgility
        "STA" -> StatStamina
        "WIL" -> StatWillpower
        "LUK" -> StatLuck
        else -> Color.White
    }

    // Lógica del cronómetro
    LaunchedEffect(key1 = isTimerRunning, key2 = timeLeft) {
        if (isTimerRunning && timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        } else if (isTimerRunning && timeLeft == 0) {
            isTimerRunning = false
            onComplete() // Auto-completar
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = RpgPanel), // Fondo gris oscuro
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, questColor.copy(alpha = 0.5f)) // Borde del color del stat
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quest.title.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                // Badge de XP
                Surface(
                    color = XpBadgeGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, XpBadgeGold)
                ) {
                    Text(
                        text = "+${quest.xpReward} XP",
                        color = XpBadgeGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(text = quest.description, color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Stat Bonus y Botón
            Row(verticalAlignment = Alignment.CenterVertically) {

                // CAMBIO AQUÍ: Ya no dice "RECOMPENSA: +1", ahora dice "TIPO DE ENTRENAMIENTO"
                Text(
                    text = "TIPO: ${quest.statBonus}", // Ej: TIPO: STR
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = questColor,
                    modifier = Modifier.weight(1f)
                )

                if (isTimeQuest) {
                    Button(
                        onClick = { isTimerRunning = !isTimerRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTimerRunning) Color.Red else questColor
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(if (timeLeft == 0) "¡HECHO!" else if (isTimerRunning) "${timeLeft}s" else "INICIAR")
                    }
                } else {
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(containerColor = questColor),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("COMPLETAR")
                    }
                }
            }
        }
    }
}

// 3. Diálogo de Level Up (Mantenemos la lógica, mejoramos colores)
@Composable
fun LevelUpDialog(level: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = RpgPanel,
        title = {
            Text(
                text = "¡LEVEL UP!",
                fontWeight = FontWeight.ExtraBold,
                color = RpgNeonCyan,
                fontSize = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                Text("¡Has alcanzado el Nivel $level!", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Todos tus atributos han aumentado +1.", color = Color.LightGray)
                Text("Nuevas misiones desbloqueadas.", color = Color.Gray, fontSize = 12.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan)
            ) {
                Text("CONTINUAR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        icon = {
            Text("⬆️", fontSize = 40.sp)
        }
    )
}