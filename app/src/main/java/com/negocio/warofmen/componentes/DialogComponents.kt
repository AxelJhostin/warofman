package com.negocio.warofmen.componentes

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.RpgNeonCyan
import com.negocio.warofmen.ui.theme.RpgPanel

@Composable
fun LevelUpDialog(level: Int, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = RpgPanel,
        title = { Text(text = "¡LEVEL UP!", fontWeight = FontWeight.ExtraBold, color = RpgNeonCyan, fontSize = 24.sp, modifier = Modifier.fillMaxWidth()) },
        text = { Column { Text("¡Has alcanzado el Nivel $level!", color = Color.White); Spacer(modifier = Modifier.height(8.dp)); Text("Todos tus atributos han aumentado +1.", color = Color.LightGray) } },
        confirmButton = { Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan)) { Text("CONTINUAR", color = Color.Black, fontWeight = FontWeight.Bold) } },
        icon = { Text("⬆️", fontSize = 40.sp) }
    )
}

@Composable
fun WeightUpdateDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightInput by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Peso") },
        text = { OutlinedTextField(value = weightInput, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) weightInput = it }, label = { Text("Peso en kg") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true) },
        confirmButton = { Button(onClick = { val w = weightInput.toFloatOrNull(); if (w != null && w > 0) onConfirm(w) }) { Text("GUARDAR") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}