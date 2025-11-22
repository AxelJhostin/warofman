package com.negocio.warofmen.vista

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.negocio.warofmen.viewmodel.CreationViewModel

@Composable
fun PantallaCreacion(viewModel: CreationViewModel,
                     onFinished: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("H") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Creación de Personaje", style = MaterialTheme.typography.headlineMedium)
        Text(text = "Tus datos reales definen tus stats iniciales", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nombre, onValueChange = { nombre = it },
            label = { Text("Nombre de Héroe") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = edad, onValueChange = { if (it.all { char -> char.isDigit() }) edad = it },
                label = { Text("Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            OutlinedTextField(
                value = peso, onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) peso = it },
                label = { Text("Peso (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }

        OutlinedTextField(
            value = altura, onValueChange = { if (it.all { char -> char.isDigit() }) altura = it },
            label = { Text("Altura (cm)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { genero = "H" }, colors = ButtonDefaults.buttonColors(containerColor = if(genero == "H") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Guerrero") }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { genero = "M" }, colors = ButtonDefaults.buttonColors(containerColor = if(genero == "M") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) { Text("Amazona") }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val w = peso.toFloatOrNull() ?: 70f
                val h = altura.toFloatOrNull() ?: 170f
                val a = edad.toIntOrNull() ?: 25
                viewModel.createCharacter(nombre, w, h, a, genero) {
                    onFinished() // Avisamos al MainActivity que termine
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = nombre.isNotEmpty() && peso.isNotEmpty() && altura.isNotEmpty()
        ) {
            Text("COMENZAR AVENTURA")
        }
    }
}