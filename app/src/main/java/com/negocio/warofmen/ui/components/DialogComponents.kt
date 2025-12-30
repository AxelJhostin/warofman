package com.negocio.warofmen.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
// 1. LEVEL UP ÉPICO (REEMPLAZADO)
// ----------------------------------------------------------------
@Composable
fun LevelUpDialog(level: Int, onDismiss: () -> Unit) {
    // Estado para la animación de escala (Pop-up)
    val scale = remember { Animatable(0f) }

    // Lanzar animación al abrir
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Usamos 'Dialog' con propiedades full-screen para quitar los márgenes por defecto
    Dialog(
        onDismissRequest = { /* No permitir cerrar tocando fuera para forzar ver la celebración */ },
        properties = DialogProperties(usePlatformDefaultWidth = false) // Ocupa todo el ancho
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f)) // Fondo oscuro inmersivo
                .clickable(enabled = false) {}, // Bloquear clicks traseros
            contentAlignment = Alignment.Center
        ) {
            // Tarjeta Neón Central
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .scale(scale.value) // Aplicamos la animación
                    .border(2.dp, RpgNeonCyan, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = RpgPanel),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // TÍTULO ANIMADO
                    Text(
                        text = "¡LEVEL UP!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = RpgNeonCyan,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "NIVEL $level",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    Divider(color = RpgNeonCyan.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(24.dp))

                    // LISTA DE STATS (Simulamos la subida de stats)
                    StatRow("FUERZA (STR)", "+1")
                    StatRow("AGILIDAD (AGI)", "+1")
                    StatRow("RESISTENCIA (STA)", "+1")
                    StatRow("VOLUNTAD (WIL)", "+1")

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTÓN DE CONTINUAR
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan),
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "CONTINUAR",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Componente auxiliar para las filas de stats
@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(text = value, color = XpBarGreen, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
    }
}

// ----------------------------------------------------------------
// 2. DIÁLOGO DE PESO (SE MANTIENE IGUAL)
// ----------------------------------------------------------------
@Composable
fun WeightUpdateDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = RpgPanel, // Aseguramos el color del tema
        title = { Text("Actualizar Peso", color = Color.White, fontWeight = FontWeight.Bold) },
        text = {
            OutlinedTextField(
                value = weightInput,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) weightInput = it },
                label = { Text("Peso en kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RpgNeonCyan,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = RpgNeonCyan
                )
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightInput.toFloatOrNull()
                    if (w != null && w > 0) onConfirm(w)
                },
                colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan)
            ) {
                Text("GUARDAR", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}