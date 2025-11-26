package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.BorderStroke
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
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.ui.theme.* // Importamos los colores globales
import com.negocio.warofmen.core.util.GameUtils // Importamos utilidades matemáticas
import com.negocio.warofmen.ui.components.BioMetricCard
import com.negocio.warofmen.ui.components.RpgStatBar
import com.negocio.warofmen.ui.components.WeightChart
import com.negocio.warofmen.ui.components.WeightUpdateDialog

@Composable
fun PantallaEstadisticas(
    player: PlayerCharacter,
    onBack: () -> Unit,
    onUpdateWeight: (Float) -> Unit,
    onNavigateToCharts: () -> Unit
) {
    // Estado para mostrar/ocultar el diálogo de peso
    var showWeightDialog by remember { mutableStateOf(false) }

    // Lógica del Popup (Usando el componente importado)
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
            .background(RpgBackground) // Color global
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

                // Usamos los componentes importados con colores globales
                RpgStatBar("FUERZA (STR)", player.strength, StatStrength)
                RpgStatBar("AGILIDAD (AGI)", player.agility, StatAgility)
                RpgStatBar("RESISTENCIA (STA)", player.stamina, StatStamina)
                RpgStatBar("VOLUNTAD (WIL)", player.willpower, StatWillpower)
                RpgStatBar("SUERTE (LUK)", player.luck, StatLuck)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. SECCIÓN DE PESO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HISTORIAL DE PESO",
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

        // Usamos el componente importado de la gráfica
        WeightChart(history = player.weightHistory)

        Spacer(modifier = Modifier.height(16.dp))

        // 4. DATOS BIOMÉTRICOS
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Usamos componente importado
            BioMetricCard("PESO ACTUAL", "${player.weight} kg")
            BioMetricCard("IMC", "%.1f".format(player.bmi))

            // Lógica con GameUtils
            val firstWeight = GameUtils.getWeightFromHistory(player.weightHistory.firstOrNull())
            val diff = player.weight - firstWeight
            val sign = if (diff > 0) "+" else ""
            BioMetricCard("CAMBIO", "$sign%.1f kg".format(diff))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. BOTÓN A GRÁFICAS DE EJERCICIO
        Button(
            onClick = onNavigateToCharts,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RpgPanel),
            border = BorderStroke(1.dp, RpgNeonCyan),
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