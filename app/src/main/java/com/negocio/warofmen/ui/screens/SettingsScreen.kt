package com.negocio.warofmen.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.SettingsViewModel
import java.util.Calendar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onResetComplete: () -> Unit
) {
    // 1. Observamos el estado real de las notificaciones (Hora y Switch)
    val notifState by viewModel.notificationState.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Función para abrir el Reloj Nativo de Android
    fun openTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Al elegir hora, actualizamos el ViewModel
                viewModel.updateNotifications(
                    isEnabled = true, // Si cambia la hora, asumimos que quiere activarlo
                    hour = hourOfDay,
                    minute = minute
                )
            },
            notifState.hour, // Hora inicial
            notifState.minute, // Minuto inicial
            true // Formato 24h
        )
        timePickerDialog.show()
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            containerColor = RpgPanel,
            title = { Text("¿Reiniciar Progreso?", color = Color.Red, fontWeight = FontWeight.Bold) },
            text = { Text("Se borrará tu personaje, nivel e historial. Esta acción no se puede deshacer.", color = Color.LightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        showResetDialog = false
                        viewModel.resetProgress { onResetComplete() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("BORRAR TODO") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) { Text("Cancelar") }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            // .statusBarsPadding() <--- YA NO ES NECESARIO (Lo maneja MainActivity)
            .padding(16.dp)
    ) {
        // HEADER
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
            Text("AJUSTES", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN NOTIFICACIONES ---
        Text("RECORDATORIO DIARIO", color = RpgNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(Modifier.padding(16.dp)) {
                // Fila 1: Título y Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.LightGray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Activar Avisos", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))

                    Switch(
                        checked = notifState.isEnabled,
                        onCheckedChange = { isChecked ->
                            // Guardamos el cambio de switch manteniendo la hora actual
                            viewModel.updateNotifications(isChecked, notifState.hour, notifState.minute)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RpgNeonCyan,
                            checkedTrackColor = RpgPanel,
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = RpgPanel
                        )
                    )
                }

                // Fila 2: Selector de Hora (Solo visible si está activo)
                if (notifState.isEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openTimePicker() }, // <--- Clic para cambiar hora
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Hora del aviso", color = Color.Gray, fontSize = 12.sp)
                            Text("Toca para cambiar", color = Color.DarkGray, fontSize = 10.sp)
                        }

                        // Formato de hora 00:00
                        val formattedTime = String.format("%02d:%02d", notifState.hour, notifState.minute)

                        Text(
                            text = formattedTime,
                            color = RpgNeonCyan,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN PELIGRO ---
        Text("ZONA DE PELIGRO", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(8.dp))
            Text("REINICIAR PROGRESO", color = Color.Red, fontWeight = FontWeight.Bold)
        }
    }
}