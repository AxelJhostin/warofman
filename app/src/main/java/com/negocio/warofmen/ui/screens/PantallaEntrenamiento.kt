package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.data.model.ExerciseType
import com.negocio.warofmen.ui.theme.RpgBackground
import com.negocio.warofmen.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

// Color Naranja Deportivo (Estilo Thenics/Nike)
val SportOrange = Color(0xFFFF6D00)

@Composable
fun PantallaEntrenamiento(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val quest = viewModel.activeQuest.collectAsState().value ?: return

    // Estados de la sesión
    var currentSetIndex by remember { mutableStateOf(0) }
    var isResting by remember { mutableStateOf(false) }
    var restTimer by remember { mutableStateOf(quest.restSeconds) }

    // Valor actual (Reps o Segundos)
    var currentTargetValue by remember { mutableStateOf(quest.sets[currentSetIndex]) }

    // Lógica del Timer (Para descanso o para ejercicios de tiempo)
    var isTimerActive by remember { mutableStateOf(false) }

    // Efecto de Timer
    LaunchedEffect(isTimerActive, isResting) {
        while (isTimerActive && currentTargetValue > 0 && !isResting) {
            delay(1000L)
            currentTargetValue--
        }
        while (isResting && restTimer > 0) {
            delay(1000L)
            restTimer--
            if (restTimer == 0) {
                isResting = false
                restTimer = quest.restSeconds
                // Avanzamos al siguiente set automáticamente al terminar descanso
                if (currentSetIndex < quest.sets.size - 1) {
                    currentSetIndex++
                    currentTargetValue = quest.sets[currentSetIndex]
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- HEADER ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(quest.title, color = Color.White, fontWeight = FontWeight.Bold)
                Text("Serie ${currentSetIndex + 1} de ${quest.sets.size}", color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(48.dp)) // Balancear layout
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- VISUALIZADOR DE SERIES (2 3 3 2 4) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            quest.sets.forEachIndexed { index, value ->
                Text(
                    text = "$value",
                    color = if (index == currentSetIndex) SportOrange else if (index < currentSetIndex) Color.Gray else Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (index == currentSetIndex) 28.sp else 20.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- ÁREA CENTRAL (BOTÓN GIGANTE) ---

        if (isResting) {
            // PANTALLA DE DESCANSO
            Text("DESCANSO", color = SportOrange, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("$restTimer s", color = Color.White, fontSize = 60.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isResting = false
                    restTimer = quest.restSeconds
                    if (currentSetIndex < quest.sets.size - 1) {
                        currentSetIndex++
                        currentTargetValue = quest.sets[currentSetIndex]
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
            ) {
                Text("SALTAR DESCANSO")
            }
        } else {
            // PANTALLA DE TRABAJO
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(SportOrange)
                    .clickable {
                        // Si es timer, toggle start/stop
                        if (quest.type == ExerciseType.TIMER) {
                            isTimerActive = !isTimerActive
                        }
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentTargetValue",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = if (quest.type == ExerciseType.REPS) "REPS" else "SEGUNDOS",
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Bold
                    )

                    if (quest.type == ExerciseType.TIMER && !isTimerActive) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
            }

            // CONTROLES +/- (Solo para Reps)
            if (quest.type == ExerciseType.REPS) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { if(currentTargetValue > 0) currentTargetValue-- }) {
                        Text("-", color = Color.Gray, fontSize = 40.sp)
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                    IconButton(onClick = { currentTargetValue++ }) {
                        Text("+", color = Color.Gray, fontSize = 40.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // --- BOTÓN INFERIOR (LISTO / TERMINAR) ---
        if (!isResting) {
            Button(
                onClick = {
                    if (quest.type == ExerciseType.TIMER && currentTargetValue > 0) {
                        // Si es timer y no ha acabado, no dejar terminar (o avisar)
                        isTimerActive = !isTimerActive
                    } else {
                        // TERMINAR SERIE
                        if (currentSetIndex < quest.sets.size - 1) {
                            // Toca descanso
                            isResting = true
                            isTimerActive = false // Reset timer logic
                        } else {
                            // FIN DEL ENTRENAMIENTO
                            viewModel.completeQuest(quest) // Dar XP
                            onNavigateBack() // Volver al Home
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = if(quest.type == ExerciseType.TIMER && isTimerActive) Color.Red else Color.DarkGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (currentSetIndex == quest.sets.size - 1) "¡TERMINAR ENTRENAMIENTO!" else if (quest.type == ExerciseType.TIMER && isTimerActive) "PAUSAR" else "LISTO (SERIE TERMINADA)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}