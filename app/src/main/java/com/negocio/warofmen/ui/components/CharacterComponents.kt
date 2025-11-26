package com.negocio.warofmen.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.*

// Barra de XP (HUD)
@Composable
fun XpProgressBar(currentXp: Int, maxXp: Int, level: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("LVL $level", fontWeight = FontWeight.ExtraBold, color = RpgNeonCyan, fontSize = 16.sp)
            Text("$currentXp / $maxXp XP", style = MaterialTheme.typography.labelMedium, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(modifier = Modifier.fillMaxWidth().height(14.dp).clip(CutCornerShape(bottomEnd = 8.dp)).background(Color.Black).border(1.dp, Color.DarkGray, CutCornerShape(bottomEnd = 8.dp))) {
            val progress = (currentXp.toFloat() / maxXp.toFloat()).coerceIn(0f, 1f)
            Box(modifier = Modifier.fillMaxWidth(progress).fillMaxHeight().background(brush = Brush.horizontalGradient(colors = listOf(XpBarGreen.copy(alpha = 0.6f), XpBarGreen))))
        }
    }
}

// Barra de Estadísticas (Fuerza, Agilidad, etc)
@Composable
fun RpgStatBar(label: String, value: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = "$value", color = color, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        val progress = (value % 50) / 50f
        val visualProgress = if (value > 0 && progress == 0f) 1f else progress
        Box(modifier = Modifier.fillMaxWidth().height(12.dp).clip(CutCornerShape(bottomEnd = 8.dp)).background(Color.Black)) {
            Box(modifier = Modifier.fillMaxWidth(if(value > 0) visualProgress + 0.05f else 0f).fillMaxHeight().background(brush = Brush.horizontalGradient(colors = listOf(color.copy(alpha = 0.5f), color))))
        }
    }
}

// Tarjeta de Datos Biométricos
@Composable
fun BioMetricCard(title: String, value: String) {
    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)), modifier = Modifier.width(105.dp)) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

// Tarjeta de Selección de Clase (Creación)
@Composable
fun ClassSelectionCard(
    title: String,
    bonus: String,
    icon: ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) color else Color.DarkGray
    val containerColor = if (isSelected) color.copy(alpha = 0.15f) else RpgPanel

    Box(
        modifier = modifier.height(140.dp).clip(RoundedCornerShape(12.dp)).background(containerColor).border(2.dp, borderColor, RoundedCornerShape(12.dp)).clickable { onClick() }.padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = null, tint = if (isSelected) color else Color.Gray, modifier = Modifier.size(40.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, color = if (isSelected) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = bonus, color = if (isSelected) color else Color.Gray, fontSize = 11.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
        }
    }
}