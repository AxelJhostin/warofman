package com.negocio.warofmen.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.viewmodel.CreationViewModel

@Composable
fun PantallaCreacion(
    viewModel: CreationViewModel,
    onFinished: () -> Unit
) {
    // Estados del formulario
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("H") } // H = Guerrero, M = Amazona

    // Colores para los inputs (Estilo Neon)
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = RpgNeonCyan,
        unfocusedBorderColor = Color.Gray,
        focusedLabelColor = RpgNeonCyan,
        unfocusedLabelColor = Color.Gray,
        cursorColor = RpgNeonCyan,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.LightGray,
        focusedContainerColor = RpgPanel,
        unfocusedContainerColor = RpgPanel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground) // Fondo Negro/Gris
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // TÍTULO ÉPICO
        Text(
            text = "FORJA TU HÉROE",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp
        )
        Text(
            text = "Tu realidad física define tu destino virtual",
            style = MaterialTheme.typography.bodyMedium,
            color = RpgNeonCyan,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // INPUT NOMBRE
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del Personaje") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputColors,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // SELECTOR DE CLASE (GÉNERO)
        Text("ELIGE TU CLASE", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            // Tarjeta Guerrero
            ClassSelectionCard(
                title = "GUERRERO",
                bonus = "+2 Fuerza\n+1 Resistencia",
                icon = Icons.Default.Person,
                isSelected = genero == "H",
                color = StatStrength, // Rojo
                onClick = { genero = "H" },
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )

            // Tarjeta Amazona
            ClassSelectionCard(
                title = "AMAZONA",
                bonus = "+2 Agilidad\n+1 Voluntad",
                icon = Icons.Default.Face, // Puedes cambiar iconos luego
                isSelected = genero == "M",
                color = StatAgility, // Verde
                onClick = { genero = "M" },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // DATOS BIOMÉTRICOS (Grid 2x2)
        Text("SINCRONIZACIÓN BIOMÉTRICA", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = edad,
                onValueChange = { if (it.all { char -> char.isDigit() }) edad = it },
                label = { Text("Edad") },
                modifier = Modifier.weight(1f).padding(end = 6.dp),
                colors = inputColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = peso,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) peso = it },
                label = { Text("Peso (kg)") },
                modifier = Modifier.weight(1f).padding(start = 6.dp),
                colors = inputColors,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = altura,
            onValueChange = { if (it.all { char -> char.isDigit() }) altura = it },
            label = { Text("Altura (cm)") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputColors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // BOTÓN FINAL
        val isFormValid = nombre.isNotEmpty() && peso.isNotEmpty() && altura.isNotEmpty() && edad.isNotEmpty()

        Button(
            onClick = {
                val w = peso.toFloatOrNull() ?: 70f
                val h = altura.toFloatOrNull() ?: 170f
                val a = edad.toIntOrNull() ?: 25
                viewModel.createCharacter(nombre, w, h, a, genero) {
                    onFinished()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFormValid) RpgNeonCyan else Color.DarkGray,
                contentColor = if (isFormValid) Color.Black else Color.Gray
            ),
            shape = RoundedCornerShape(8.dp),
            enabled = isFormValid
        ) {
            Text(
                text = "COMENZAR AVENTURA",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// COMPONENTE PERSONALIZADO: Tarjeta de Selección de Clase
@Composable
fun ClassSelectionCard(
    title: String,
    bonus: String,
    icon: ImageVector,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) color else Color.DarkGray
    val containerColor = if (isSelected) color.copy(alpha = 0.15f) else RpgPanel

    Box(
        modifier = modifier
            .height(140.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) color else Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bonus,
                color = if (isSelected) color else Color.Gray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}