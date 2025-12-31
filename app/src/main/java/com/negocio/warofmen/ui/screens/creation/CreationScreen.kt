package com.negocio.warofmen.ui.screens.creation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.CreationViewModel

@Composable
fun CreationScreen(
    viewModel: CreationViewModel,
    onFinished: () -> Unit
) {
    // Estados del Formulario
    var currentStep by remember { mutableIntStateOf(0) }

    // Datos del Personaje
    var nombre by remember { mutableStateOf("") }
    var genero by remember { mutableStateOf("H") }
    var edad by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }

    // Datos Avanzados
    var hasTapeMeasure by remember { mutableStateOf(false) }
    var cuello by remember { mutableStateOf("") }
    var cintura by remember { mutableStateOf("") }
    var cadera by remember { mutableStateOf("") }

    // Títulos de las Fases para el Header
    val phaseTitles = listOf(
        "FASE 1: IDENTIDAD DEL AGENTE",
        "FASE 2: BIOMETRÍA BÁSICA",
        "FASE 3: ESCANEO AVANZADO",
        "FASE 4: CONFIRMACIÓN"
    )

    // Estilo de Inputs unificado
    val inputColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = RpgNeonCyan,
        unfocusedBorderColor = Color.DarkGray,
        cursorColor = RpgNeonCyan,
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.LightGray,
        focusedLabelColor = RpgNeonCyan,
        unfocusedLabelColor = Color.Gray,
        focusedContainerColor = RpgPanel.copy(alpha = 0.5f),
        unfocusedContainerColor = RpgPanel.copy(alpha = 0.5f)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp)
            .imePadding() // Manejo de teclado
    ) {
        // 1. BARRA DE PROGRESO Y TÍTULO
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            WizardProgressBar(currentStep = currentStep, totalSteps = 4)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = phaseTitles[currentStep],
                color = RpgNeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. CONTENIDO CENTRAL CON TRANSICIONES
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            AnimatedContent(
                targetState = currentStep,
                label = "WizardTransition",
                transitionSpec = {
                    if (targetState > initialState) {
                        // Avanzar: Entra por derecha, sale por izquierda
                        slideInHorizontally(animationSpec = tween(500)) { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally(animationSpec = tween(500)) { width -> -width } + fadeOut()
                    } else {
                        // Retroceder: Entra por izquierda, sale por derecha
                        slideInHorizontally(animationSpec = tween(500)) { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally(animationSpec = tween(500)) { width -> width } + fadeOut()
                    }
                }
            ) { step ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (step) {
                        0 -> StepIdentity(
                            nombre, { nombre = it },
                            genero, { genero = it },
                            edad, { edad = it },
                            inputColors
                        )
                        1 -> StepBodyBasic(
                            peso, { peso = it },
                            altura, { altura = it },
                            inputColors
                        )
                        2 -> StepBodyAdvanced(
                            genero, hasTapeMeasure, { hasTapeMeasure = it },
                            cuello, { cuello = it },
                            cintura, { cintura = it },
                            cadera, { cadera = it },
                            inputColors, altura, peso
                        )
                        3 -> StepSummary(
                            nombre, genero, edad, peso, altura, cuello, cintura, cadera,
                            onConfirm = {
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
                            }
                        )
                    }
                }
            }
        }

        // 3. BOTONES DE NAVEGACIÓN
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Botón Atrás (Invisible en paso 0)
            if (currentStep > 0) {
                OutlinedButton(
                    onClick = { currentStep-- },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                    shape = CutCornerShape(8.dp), // Forma Cyberpunk
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)
                ) {
                    Text("ATRÁS")
                }
            } else {
                Spacer(modifier = Modifier.width(10.dp))
            }

            // Botón Siguiente
            if (currentStep < 3) {
                val canAdvance = when (currentStep) {
                    0 -> nombre.isNotEmpty() && edad.isNotEmpty()
                    1 -> peso.isNotEmpty() && altura.isNotEmpty()
                    2 -> true
                    else -> false
                }

                Button(
                    onClick = { currentStep++ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAdvance) RpgNeonCyan else Color.DarkGray.copy(alpha = 0.5f)
                    ),
                    shape = CutCornerShape(8.dp), // Forma Cyberpunk
                    enabled = canAdvance,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Text(
                        text = "SIGUIENTE",
                        color = if (canAdvance) Color.Black else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// --- COMPONENTE VISUAL MEJORADO: BARRA DE PROGRESO CONECTADA ---
@Composable
fun WizardProgressBar(currentStep: Int, totalSteps: Int) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        // Línea de fondo (Gris)
        Divider(
            color = Color.DarkGray,
            thickness = 2.dp,
            modifier = Modifier.fillMaxWidth()
        )

        // Línea de progreso (Neon) - Calculamos el ancho basado en el paso actual
        Row(modifier = Modifier.fillMaxWidth()) {
            val progressFraction = currentStep.toFloat() / (totalSteps - 1)
            Box(
                modifier = Modifier
                    .fillMaxWidth(progressFraction)
                    .height(2.dp)
                    .background(RpgNeonCyan)
            )
        }

        // Nodos (Círculos)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(totalSteps) { index ->
                val isActive = index <= currentStep
                val isCurrent = index == currentStep

                Box(
                    modifier = Modifier
                        .size(if (isCurrent) 16.dp else 12.dp)
                        .background(if (isActive) RpgNeonCyan else RpgBackground, CircleShape)
                        .border(
                            width = 2.dp,
                            color = if (isActive) RpgNeonCyan else Color.DarkGray,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}