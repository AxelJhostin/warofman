package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.ui.components.BioMetricCard
import com.negocio.warofmen.ui.components.StatBox
import com.negocio.warofmen.ui.components.WeightChart
import com.negocio.warofmen.ui.theme.*

@Composable
fun PantallaEstadisticas(
    player: PlayerCharacter,
    onBack: () -> Unit,
    onUpdateWeight: (Float) -> Unit,
    onNavigateToCharts: () -> Unit
) {
    var showWeightDialog by remember { mutableStateOf(false) }
    var newWeightInput by remember { mutableStateOf("") }

    // --- DIÁLOGO PARA REGISTRAR PESO ---
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            containerColor = RpgPanel,
            title = { Text("Registrar Peso", color = RpgNeonCyan) },
            text = {
                OutlinedTextField(
                    value = newWeightInput,
                    onValueChange = { newWeightInput = it },
                    label = { Text("Nuevo peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RpgNeonCyan,
                        focusedTextColor = Color.White
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = newWeightInput.toFloatOrNull()
                        if (w != null && w > 0) {
                            onUpdateWeight(w)
                            showWeightDialog = false
                            newWeightInput = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan)
                ) { Text("Guardar", color = Color.Black) }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // --- CONTENEDOR PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp)
    ) {
        // 1. HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("ESTADÍSTICAS", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showWeightDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("PESO", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // USAMOS LAZY COLUMN PARA QUE TODO SEA SCROLLEABLE Y ORDENADO
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ITEM 1: GRÁFICA DE EVOLUCIÓN
            item {
                Text("EVOLUCIÓN DE PESO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = RpgPanel),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("ACTUAL", color = Color.Gray, fontSize = 10.sp)
                                Text("${player.currentWeight} kg", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("IMC", color = Color.Gray, fontSize = 10.sp)
                                val bmiColor = if(player.currentBmi < 25) XpBarGreen else if(player.currentBmi < 30) StatStamina else StatStrength
                                Text(String.format("%.1f", player.currentBmi), color = bmiColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        WeightChart(history = player.measurementLogs)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ITEM 2: STATS RPG (Fuerza, Agilidad, etc.)
            item {
                Text("ATRIBUTOS DE COMBATE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Row(Modifier.fillMaxWidth()) {
                    StatBox("FUERZA", "${player.strength}", StatStrength, Modifier.weight(1f))
                    StatBox("RESISTENCIA", "${player.stamina}", StatStamina, Modifier.weight(1f))
                    StatBox("AGILIDAD", "${player.agility}", StatAgility, Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth()) {
                    StatBox("VOLUNTAD", "${player.willpower}", StatWillpower, Modifier.weight(1f))
                    StatBox("SUERTE", "${player.luck}", XpBarGreen, Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f)) // Espaciador para cuadrar grid
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ITEM 3: COMPOSICIÓN CORPORAL (Tu lógica original recuperada)
            if (player.currentBodyFat != null) {
                item {
                    Text("COMPOSICIÓN CORPORAL", color = RpgNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = RpgPanel),
                        border = androidx.compose.foundation.BorderStroke(1.dp, RpgNeonCyan.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            // Grasa %
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("GRASA %", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("%.1f %%".format(player.currentBodyFat), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }

                            // Divisor vertical
                            Box(modifier = Modifier.width(1.dp).height(40.dp).background(Color.DarkGray))

                            // Masa Magra
                            val fatMass = player.currentWeight * (player.currentBodyFat / 100)
                            val leanMass = player.currentWeight - fatMass
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("MASA MAGRA", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("%.1f kg".format(leanMass), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Medidas Extra (Cintura, Cuello...)
                    val lastLog = player.measurementLogs.maxByOrNull { it.timestamp }
                    if (lastLog != null && (lastLog.waist != null || lastLog.neck != null)) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (lastLog.neck != null) BioMetricCard("CUELLO", "${lastLog.neck} cm")
                            if (lastLog.waist != null) BioMetricCard("CINTURA", "${lastLog.waist} cm")
                            if (lastLog.hip != null) BioMetricCard("CADERA", "${lastLog.hip} cm")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            } else {
                // Tip si no hay datos
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C).copy(alpha = 0.5f))) {
                        Text("💡 Tip: Usa cinta métrica al registrar peso para ver % Grasa.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ITEM 4: BOTÓN GRÁFICAS DE RENDIMIENTO
            item {
                Button(
                    onClick = onNavigateToCharts,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RpgPanel),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RpgNeonCyan),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("📈  VER RENDIMIENTO DE EJERCICIOS", color = RpgNeonCyan, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // ITEM 5: HISTORIAL DETALLADO (LISTA)
            item {
                Text("HISTORIAL DE PESO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Aquí iteramos la lista dentro del LazyColumn
            val history = player.measurementLogs.sortedByDescending { it.timestamp }
            items(history) { log ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(RpgPanel, RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = GameUtils.formatDate(log.timestamp), color = Color.LightGray)
                    Text(text = "${log.weight} kg", color = RpgNeonCyan, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}