package com.negocio.warofmen.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build // Para STR
import androidx.compose.material.icons.filled.Face // Para LUK/Social
import androidx.compose.material.icons.filled.Favorite // Para STA
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star // Para WIL
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.data.model.Quest
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.core.util.GameUtils
import kotlinx.coroutines.delay

@Composable
fun QuestCard(quest: Quest, onComplete: () -> Unit) {
    // Lógica del Timer (Se mantiene igual por si la usas, aunque la UI sugiere ir a otra pantalla)
    val isTimeQuest = quest.description.contains("segundos") || quest.description.contains("Wall-sit")
    var timeLeft by remember { mutableStateOf(if (isTimeQuest) GameUtils.extractSeconds(quest.description) else 0) }
    var isTimerRunning by remember { mutableStateOf(false) }

    // Color Temático según Stat
    val questColor = when(quest.statBonus) {
        "STR" -> StatStrength
        "AGI" -> StatAgility
        "STA" -> StatStamina
        "WIL" -> StatWillpower
        "LUK" -> StatLuck
        else -> Color.White
    }

    LaunchedEffect(key1 = isTimerRunning, key2 = timeLeft) {
        if (isTimerRunning && timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else if (isTimerRunning && timeLeft == 0) {
            isTimerRunning = false
            onComplete()
        }
    }

    // --- DISEÑO DE TARJETA TECH ---
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        shape = RoundedCornerShape(12.dp), // Bordes suaves
        border = BorderStroke(1.dp, questColor.copy(alpha = 0.4f)) // Borde de neón sutil
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 1. ICONO DE CLASE (CAJA IZQUIERDA)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(questColor.copy(alpha = 0.2f), Color.Transparent)
                        )
                    )
                    .border(1.dp, questColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getStatIcon(quest.statBonus),
                    contentDescription = null,
                    tint = questColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. INFORMACIÓN CENTRAL
            Column(modifier = Modifier.weight(1f)) {
                // Título
                Text(
                    text = quest.title.uppercase(),
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descripción corta
                Text(
                    text = quest.description,
                    color = Color.Gray,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Badge de Tipo de Stat
                Surface(
                    color = questColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = "BONUS: ${quest.statBonus}",
                        color = questColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 3. COLUMNA DE ACCIÓN (DERECHA)
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.height(56.dp) // Misma altura que el icono
            ) {
                // Badge XP
                Surface(
                    color = XpBadgeGold.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, XpBadgeGold.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "+${quest.xpReward} XP",
                        color = XpBadgeGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }

                // Botón Play
                FilledIconButton(
                    onClick = onComplete,
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = questColor
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Iniciar",
                        tint = Color.Black, // Contraste neón/negro
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// Helper para obtener iconos según el Stat
private fun getStatIcon(stat: String): ImageVector {
    return when (stat) {
        "STR" -> Icons.Default.Build     // Pesa / Martillo
        "AGI" -> Icons.Default.KeyboardArrowRight // Velocidad / Flecha (Temporal, busca un rayo si puedes)
        "STA" -> Icons.Default.Favorite  // Corazón / Resistencia
        "WIL" -> Icons.Default.Star      // Estrella / Mente
        "LUK" -> Icons.Default.Face      // Suerte / Carisma
        else -> Icons.Default.Add
    }
}