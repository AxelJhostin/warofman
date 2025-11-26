package com.negocio.warofmen.ui.screens.creation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.CreationViewModel

// NOTA: Al mover el archivo, MainActivity puede dar error en el import.
// Solo ve a MainActivity y actualiza el import a: com.negocio.warofmen.ui.screens.creation.PantallaCreacion

@Composable
fun CreationScreen(
    viewModel: CreationViewModel,
    onFinished: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }

    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("H") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

    var hasTapeMeasure by remember { mutableStateOf(false) }
    var cuello by remember { mutableStateOf("") }
    var cintura by remember { mutableStateOf("") }
    var cadera by remember { mutableStateOf("") }

    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = RpgNeonCyan,
        unfocusedBorderColor = Color.Gray,
        cursorColor = RpgNeonCyan,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.LightGray,
        focusedContainerColor = RpgPanel,
        unfocusedContainerColor = RpgPanel
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp)
    ) {
        WizardProgressBar(currentStep = currentStep, totalSteps = 4)
        Spacer(modifier = Modifier.height(16.dp))

        AnimatedContent(targetState = currentStep, label = "Wizard") { step ->
            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
                when (step) {
                    0 -> StepIdentity(nombre, { nombre = it }, genero, { genero = it }, edad, { edad = it }, inputColors)
                    1 -> StepBodyBasic(peso, { peso = it }, altura, { altura = it }, inputColors)
                    2 -> StepBodyAdvanced(genero, hasTapeMeasure, { hasTapeMeasure = it }, cuello, { cuello = it }, cintura, { cintura = it }, cadera, { cadera = it }, inputColors, altura, peso)
                    3 -> StepSummary(nombre, genero, edad, peso, altura, cuello, cintura, cadera, onConfirm = {
                        val w = peso.toFloatOrNull() ?: 70f
                        val h = altura.toFloatOrNull() ?: 170f
                        val a = edad.toIntOrNull() ?: 25
                        val n = cuello.toFloatOrNull()
                        val c = cintura.toFloatOrNull()
                        val hip = cadera.toFloatOrNull()

                        viewModel.createCharacter(
                            name = nombre, gender = genero, age = a, height = h, weight = w,
                            neck = n, waist = c, hip = hip,
                            onResult = { onFinished() }
                        )
                    })
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            if (currentStep > 0) {
                OutlinedButton(onClick = { currentStep-- }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)) { Text("Atrás") }
            } else { Spacer(modifier = Modifier.width(10.dp)) }

            if (currentStep < 3) {
                val canAdvance = when(currentStep) {
                    0 -> nombre.isNotEmpty() && edad.isNotEmpty()
                    1 -> peso.isNotEmpty() && altura.isNotEmpty()
                    2 -> true
                    else -> false
                }
                Button(onClick = { currentStep++ }, colors = ButtonDefaults.buttonColors(containerColor = if(canAdvance) RpgNeonCyan else Color.DarkGray), enabled = canAdvance) {
                    Text("Siguiente", color = if(canAdvance) Color.Black else Color.Gray)
                }
            }
        }
    }
}