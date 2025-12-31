package com.negocio.warofmen.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.negocio.warofmen.ui.theme.*
import kotlinx.coroutines.delay

// ----------------------------------------------------------------
// 1. LEVEL UP ÉPICO (MEJORADO CON PULSO Y ESTILO TECH)
// ----------------------------------------------------------------
@Composable
fun LevelUpDialog(level: Int, onDismiss: () -> Unit) {
    // Animación de Entrada (Pop-up)
    val scale = remember { Animatable(0f) }

    // Animación de Pulso Continuo para el Nivel
    val infiniteTransition = rememberInfiniteTransition(label = "Pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "PulseAnim"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Dialog(
        onDismissRequest = { /* Bloqueado */ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .clickable(enabled = false) {},
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .scale(scale.value)
                    .border(2.dp, RpgNeonCyan, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RpgPanel),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ÍCONO DECORATIVO SUPERIOR
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(RpgNeonCyan.copy(alpha = 0.2f), CircleShape)
                            .border(2.dp, RpgNeonCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowUp,
                            contentDescription = null,
                            tint = RpgNeonCyan,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "¡LEVEL UP!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = RpgNeonCyan,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // NÚMERO DE NIVEL PULSANTE
                    Text(
                        text = "$level",
                        fontSize = 72.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.scale(pulseScale) // Aplica el pulso
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Separador con degradado
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color.Transparent, RpgNeonCyan, Color.Transparent)
                                )
                            )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // STATS ESTILIZADOS
                    StatRow("FUERZA (STR)", "+1")
                    StatRow("AGILIDAD (AGI)", "+1")
                    StatRow("RESISTENCIA (STA)", "+1")
                    StatRow("VOLUNTAD (WIL)", "+1")

                    Spacer(modifier = Modifier.height(32.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = "CONTINUAR",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .border(1.dp, Color.DarkGray.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text(text = value, color = XpBarGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
    }
}

// ----------------------------------------------------------------
// 2. DIÁLOGO DE PESO (REDDISEÑADO CUSTOM)
// ----------------------------------------------------------------
@Composable
fun WeightUpdateDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightInput by remember { mutableStateOf("") }

    // Usamos Dialog en lugar de AlertDialog para control total del estilo
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, RpgNeonCyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ACTUALIZAR PESO",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) weightInput = it },
                    label = { Text("Nuevo Peso (kg)") },
                    placeholder = { Text("Ej: 75.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RpgNeonCyan,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = RpgNeonCyan,
                        focusedLabelColor = RpgNeonCyan,
                        unfocusedLabelColor = Color.Gray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCELAR", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            val w = weightInput.toFloatOrNull()
                            if (w != null && w > 0) onConfirm(w)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("GUARDAR", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}