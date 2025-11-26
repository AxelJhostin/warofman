package com.negocio.warofmen.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.data.model.Quest
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.core.util.GameUtils
import kotlinx.coroutines.delay

@Composable
fun QuestCard(quest: Quest, onComplete: () -> Unit) {
    val isTimeQuest = quest.description.contains("segundos") || quest.description.contains("Wall-sit")
    var timeLeft by remember { mutableStateOf(if (isTimeQuest) GameUtils.extractSeconds(quest.description) else 0) }
    var isTimerRunning by remember { mutableStateOf(false) }

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

    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, questColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = quest.title.uppercase(), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                Surface(color = XpBadgeGold.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp), border = BorderStroke(1.dp, XpBadgeGold)) {
                    Text(text = "+${quest.xpReward} XP", color = XpBadgeGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = quest.description, color = Color.LightGray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "TIPO: ${quest.statBonus}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = questColor, modifier = Modifier.weight(1f))
                Button(onClick = onComplete, colors = ButtonDefaults.buttonColors(containerColor = questColor), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp), modifier = Modifier.height(36.dp)) {
                    Text("INICIAR")
                }
            }
        }
    }
}