package com.negocio.warofmen.ui.screens

import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.SettingsViewModel
import java.util.Calendar

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onResetComplete: () -> Unit
) {
    // Estado de notificaciones
    val notifState by viewModel.notificationState.collectAsState()
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Función para abrir el Reloj Nativo
    fun openTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                viewModel.updateNotifications(isEnabled = true, hour = hourOfDay, minute = minute)
            },
            notifState.hour,
            notifState.minute,
            true // 24h
        )
        timePickerDialog.show()
    }

    // --- DIÁLOGO DE REINICIO PERSONALIZADO (ALERTA ROJA) ---
    if (showResetDialog) {
        Dialog(onDismissRequest = { showResetDialog = false }) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black),
                border = BorderStroke(2.dp, Color.Red),
                shape = CutCornerShape(16.dp), // Esquinas cortadas estilo militar
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PURGA DE SISTEMA",
                        color = Color.Red,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Esta acción eliminará permanentemente a tu agente, nivel e historial. Es irreversible.",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { showResetDialog = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("CANCELAR", color = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                showResetDialog = false
                                viewModel.resetProgress { onResetComplete() }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("BORRAR", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // --- PANTALLA PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp)
    ) {
        // HEADER
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Gray)
            }
            Text(
                text = "CONFIGURACIÓN DEL SISTEMA",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN NOTIFICACIONES ---
        SettingsSectionTitle("COMUNICACIONES")

        Card(
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            border = BorderStroke(1.dp, RpgNeonCyan.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                // Switch Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(RpgNeonCyan.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = RpgNeonCyan)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Protocolo de Aviso", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Recordatorio diario de misión", color = Color.Gray, fontSize = 10.sp)
                    }

                    Switch(
                        checked = notifState.isEnabled,
                        onCheckedChange = { viewModel.updateNotifications(it, notifState.hour, notifState.minute) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = RpgNeonCyan,
                            checkedTrackColor = RpgNeonCyan.copy(alpha = 0.3f),
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = Color.DarkGray
                        )
                    )
                }

                // Reloj Digital (Solo si está activo)
                if (notifState.isEnabled) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.DarkGray.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("HORA DE SINCRONIZACIÓN", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Caja del Reloj Clickable
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, RpgNeonCyan),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { openTimePicker() }
                    ) {
                        Row(
                            modifier = Modifier.padding(vertical = 16.dp, horizontal = 24.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = String.format("%02d:%02d", notifState.hour, notifState.minute),
                                color = RpgNeonCyan,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )

                            Surface(
                                color = RpgNeonCyan,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "EDITAR",
                                    color = Color.Black,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- SECCIÓN PELIGRO ---
        SettingsSectionTitle("ZONA CRÍTICA")

        Button(
            onClick = { showResetDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f)),
            shape = CutCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red)
            Spacer(modifier = Modifier.width(12.dp))
            Text("RESTABLECER DE FÁBRICA", color = Color.Red, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.weight(1f))

        // Footer de versión
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Text("WAR OF MEN v1.0 - BUILD RELEASE", color = Color.DarkGray, fontSize = 10.sp, letterSpacing = 2.sp)
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = Color.Gray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )
}