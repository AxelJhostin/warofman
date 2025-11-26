package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.components.*
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.core.util.GameUtils

@Composable
fun PantallaEstadisticas(
    player: PlayerCharacter,
    onBack: () -> Unit,
    onUpdateWeight: (Float) -> Unit,
    onNavigateToCharts: () -> Unit
) {
    var showWeightDialog by remember { mutableStateOf(false) }

    if (showWeightDialog) {
        WeightUpdateDialog(
            onDismiss = { showWeightDialog = false },
            onConfirm = { newWeight ->
                onUpdateWeight(newWeight)
                showWeightDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) { Text("🔙") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "ESTADÍSTICAS",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. TARJETA DE PERSONAJE
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                .background(RpgPanel, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = player.name.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Nivel ${player.level} ${player.gender}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))

                RpgStatBar("FUERZA (STR)", player.strength, StatStrength)
                RpgStatBar("AGILIDAD (AGI)", player.agility, StatAgility)
                RpgStatBar("RESISTENCIA (STA)", player.stamina, StatStamina)
                RpgStatBar("VOLUNTAD (WIL)", player.willpower, StatWillpower)
                RpgStatBar("SUERTE (LUK)", player.luck, StatLuck)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. HISTORIAL DE PESO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "PROGRESO FÍSICO",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            SmallFloatingActionButton(
                onClick = { showWeightDialog = true },
                containerColor = RpgNeonCyan,
                contentColor = Color.Black
            ) {
                Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        WeightChart(history = player.measurementLogs) // Usamos la lista nueva de BodyLog

        Spacer(modifier = Modifier.height(16.dp))

        // 4. DATOS BÁSICOS (Peso e IMC)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            BioMetricCard("PESO", "${player.currentWeight} kg")

            // Color del IMC dinámico
            val bmiColor = when {
                player.currentBmi < 18.5 -> StatAgility // Azul/Verde (Delgado)
                player.currentBmi < 25 -> XpBarGreen    // Verde (Normal)
                player.currentBmi < 30 -> StatStamina   // Amarillo (Sobrepeso)
                else -> StatStrength                    // Rojo (Obesidad)
            }

            // Tarjeta IMC Personalizada con color
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)), modifier = Modifier.width(105.dp)) {
                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("IMC", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("%.1f".format(player.currentBmi), color = bmiColor, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }

            // Cambio de peso total
            val firstLog = player.measurementLogs.minByOrNull { it.timestamp }
            val firstWeight = firstLog?.weight ?: player.currentWeight
            val diff = player.currentWeight - firstWeight
            val sign = if (diff > 0) "+" else ""
            BioMetricCard("CAMBIO", "$sign%.1f kg".format(diff))
        }

        // 5. NUEVA SECCIÓN: COMPOSICIÓN CORPORAL (Solo si hay datos)
        if (player.currentBodyFat != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "COMPOSICIÓN CORPORAL",
                color = RpgNeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = RpgPanel),
                border = androidx.compose.foundation.BorderStroke(1.dp, RpgNeonCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Grasa Corporal
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("GRASA %", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "%.1f %%".format(player.currentBodyFat),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Separador vertical
                    Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.DarkGray))

                    // Masa Magra (Calculada: Peso Total - (Peso * %Grasa))
                    val fatMass = player.currentWeight * (player.currentBodyFat / 100)
                    val leanMass = player.currentWeight - fatMass

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("MASA MAGRA", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "%.1f kg".format(leanMass),
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Mostrar detalles de medidas si existen en el último log
            val lastLog = player.measurementLogs.maxByOrNull { it.timestamp }
            if (lastLog != null && (lastLog.waist != null || lastLog.neck != null)) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (lastLog.neck != null) BioMetricCard("CUELLO", "${lastLog.neck} cm")
                    if (lastLog.waist != null) BioMetricCard("CINTURA", "${lastLog.waist} cm")
                    if (lastLog.hip != null) BioMetricCard("CADERA", "${lastLog.hip} cm")
                }
            }
        } else {
            // Si no tiene datos, mostramos invitación a medirse
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C).copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "💡 Tip: Usa una cinta métrica al registrar tu peso para desbloquear el análisis de Grasa Corporal.",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    lineHeight = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 6. BOTÓN GRÁFICAS
        Button(
            onClick = onNavigateToCharts,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RpgPanel),
            border = androidx.compose.foundation.BorderStroke(1.dp, RpgNeonCyan),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "📈  VER RENDIMIENTO DE EJERCICIOS",
                color = RpgNeonCyan,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}