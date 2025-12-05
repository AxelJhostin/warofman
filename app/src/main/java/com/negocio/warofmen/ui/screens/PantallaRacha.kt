package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.data.model.StreakMilestone
import com.negocio.warofmen.data.source.MilestoneProvider
import com.negocio.warofmen.ui.theme.*

@Composable
fun PantallaRacha(
    currentStreak: Int,
    onBack: () -> Unit
) {
    val milestones = MilestoneProvider.milestones
    val currentMultiplier = MilestoneProvider.getCurrentMultiplier(currentStreak)
    val hasBlueFire = MilestoneProvider.hasBlueFire(currentStreak)

    // Color del tema (Naranja o Azul si es nivel alto)
    val themeColor = if (hasBlueFire) Color(0xFF2196F3) else Color(0xFFFF5722)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
            }
            Text("CAMINO DE LA DISCIPLINA", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TARJETA DE ESTADO ACTUAL
        Card(
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, themeColor),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("RACHA ACTUAL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("$currentStreak DÍAS", color = themeColor, fontSize = 48.sp, fontWeight = FontWeight.Black)

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = themeColor.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "BONUS ACTIVO: +${((currentMultiplier - 1) * 100).toInt()}% XP",
                        color = themeColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("HITOS Y RECOMPENSAS", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        // LISTA DE HITOS (TIMELINE)
        LazyColumn {
            items(milestones) { milestone ->
                MilestoneItem(milestone, currentStreak, themeColor)
            }
        }
    }
}

@Composable
fun MilestoneItem(milestone: StreakMilestone, currentStreak: Int, themeColor: Color) {
    val isUnlocked = currentStreak >= milestone.daysRequired
    val itemColor = if (isUnlocked) (if(milestone.isBlueFire) Color(0xFF2196F3) else Color(0xFFFF5722)) else Color.Gray

    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
        // Columna Izquierda (Línea y Círculo)
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(40.dp)) {
            // Círculo
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(if (isUnlocked) itemColor else Color.Transparent)
                    .border(2.dp, itemColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isUnlocked) {
                    Text("✓", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            // Línea conectora (visual)
            Box(modifier = Modifier.width(2.dp).height(60.dp).background(Color.DarkGray))
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Columna Derecha (Info)
        Column {
            Text(
                text = "${milestone.daysRequired} DÍAS: ${milestone.title.uppercase()}",
                color = if (isUnlocked) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = milestone.description,
                color = if (isUnlocked) Color.LightGray else Color.DarkGray,
                fontSize = 14.sp
            )

            if (milestone.isBlueFire) {
                Text("🔥 RECOMPENSA: FUEGO AZUL", color = Color(0xFF2196F3), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            } else {
                Text("⭐ RECOMPENSA: XP x${milestone.xpMultiplier}", color = if (isUnlocked) Color(0xFFFFC107) else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}