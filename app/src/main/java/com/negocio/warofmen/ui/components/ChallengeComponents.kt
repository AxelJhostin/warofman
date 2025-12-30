package com.negocio.warofmen.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
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
import com.negocio.warofmen.data.model.Challenge
import com.negocio.warofmen.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ----------------------------------------------------------------
// 1. TARJETA DE RETO ACTIVO (MODIFICADA CON LONG PRESS)
// ----------------------------------------------------------------
@OptIn(ExperimentalFoundationApi::class) // Necesario para combinedClickable
@Composable
fun ActiveChallengeCard(
    challenge: Challenge,
    currentWeight: Float,
    onLongPress: () -> Unit // <--- NUEVO CALLBACK
) {
    var timeLeftString by remember { mutableStateOf(GameUtils.formatCountdown(challenge.deadline)) }

    LaunchedEffect(key1 = challenge.deadline) {
        while (true) {
            timeLeftString = GameUtils.formatCountdown(challenge.deadline)
            delay(1000L)
        }
    }

    val progress = GameUtils.calculateChallengeProgress(
        start = challenge.startWeight,
        current = currentWeight,
        target = challenge.targetWeight
    )

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "ProgressAnim")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            // CAMBIO: Detectar pulsación larga
            .combinedClickable(
                onClick = { /* No hacemos nada al click simple */ },
                onLongClick = onLongPress // <--- AQUÍ SE ACTIVA EL BORRADO
            ),
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        border = BorderStroke(1.dp, RpgNeonCyan.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "JURAMENTO ACTIVO",
                    color = Color.Gray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Surface(
                    color = XpBadgeGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, XpBadgeGold)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = XpBadgeGold, modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${challenge.rewardXp} XP", color = XpBadgeGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body
            Text(
                text = challenge.description.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "TIEMPO RESTANTE: $timeLeftString",
                color = if (timeLeftString == "00:00:00") Color.Red else RpgNeonCyan,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                fontFamily = MaterialTheme.typography.bodyLarge.fontFamily
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = RpgNeonCyan,
                trackColor = Color.DarkGray,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Footer info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${challenge.startWeight}kg", color = Color.Gray, fontSize = 12.sp)
                Text("OBJETIVO: ${challenge.targetWeight}kg", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            // Pista visual para el usuario (opcional)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "(Mantén presionado para cancelar)",
                color = Color.Gray.copy(alpha = 0.5f),
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

// ----------------------------------------------------------------
// 2. TARJETA VACÍA (Sin cambios)
// ----------------------------------------------------------------
@Composable
fun EmptyChallengeCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = RpgPanel.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, Color.Gray.copy(alpha = 0.5f), ),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ESTABLECER JURAMENTO (RETO)", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

// ----------------------------------------------------------------
// 3. DIÁLOGO DE CREACIÓN (Con DatePicker)
// ----------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateChallengeDialog(
    onDismiss: () -> Unit,
    onCreate: (target: Float, deadline: Long, desc: String) -> Unit
) {
    var targetInput by remember { mutableStateOf("") }
    var descInput by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }

    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedDateMillis = datePickerState.selectedDateMillis
                    showDatePicker = false
                }) {
                    Text("OK", color = RpgNeonCyan)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("CANCELAR", color = Color.Gray)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = RpgPanel,
                titleContentColor = Color.White,
                headlineContentColor = RpgNeonCyan,
                weekdayContentColor = Color.Gray,
                dayContentColor = Color.White,
                selectedDayContainerColor = RpgNeonCyan,
                selectedDayContentColor = Color.Black
            )
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RpgPanel,
        title = { Text("NUEVO JURAMENTO", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Define tu meta y fecha límite. La disciplina te recompensará.", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = targetInput,
                    onValueChange = { targetInput = it },
                    label = { Text("Peso Objetivo (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RpgNeonCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val dateText = if (selectedDateMillis != null) {
                    dateFormatter.format(Date(selectedDateMillis!!))
                } else {
                    ""
                }

                OutlinedTextField(
                    value = dateText,
                    onValueChange = { },
                    label = { Text("Fecha Límite") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = RpgNeonCyan) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RpgNeonCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    modifier = Modifier.clickable { showDatePicker = true },
                    interactionSource = remember { MutableInteractionSource() }.also { interactionSource ->
                        LaunchedEffect(interactionSource) {
                            interactionSource.interactions.collect {
                                if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                    showDatePicker = true
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = descInput,
                    onValueChange = { descInput = it },
                    label = { Text("Nombre del Reto (Ej: Verano)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RpgNeonCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val target = targetInput.toFloatOrNull()
                    val deadline = selectedDateMillis
                    if (target != null && deadline != null && deadline > System.currentTimeMillis() && descInput.isNotEmpty()) {
                        onCreate(target, deadline, descInput)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan)
            ) {
                Text("ACEPTAR RETO", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCELAR", color = Color.Gray) }
        }
    )
}