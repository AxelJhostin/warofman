package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBack: () -> Unit,
    onResetComplete: () -> Unit
) {
    var showResetDialog by remember { mutableStateOf(false) }
    var vibrationEnabled by remember { mutableStateOf(true) }

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
            .padding(16.dp)
    ) {
        // HEADER MEJORADO
        Row(verticalAlignment = Alignment.CenterVertically) {
            // SOLUCIÓN 2: Botón de regreso elegante (Icono solo)
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Flecha estándar de Android
                    contentDescription = "Volver",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp) // Un poco más grande
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("AJUSTES", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // SECCIÓN GENERAL
        Text("GENERAL", color = RpgNeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        SettingItem(
            icon = Icons.Default.Notifications,
            title = "Vibración y Efectos",
            subtitle = "Feedback háptico al completar misiones",
            trailing = {
                Switch(
                    checked = vibrationEnabled,
                    onCheckedChange = { vibrationEnabled = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = RpgNeonCyan,
                        checkedTrackColor = RpgPanel,
                        uncheckedThumbColor = Color.Gray,
                        uncheckedTrackColor = RpgPanel
                    )
                )
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // SECCIÓN PELIGRO
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

@Composable
fun SettingItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = Color.LightGray)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, fontSize = 12.sp)
            }
            trailing()
        }
    }
}