package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.SoundManager
import com.negocio.warofmen.data.model.ExerciseType
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.delay

@Composable
fun PantallaEntrenamiento(
    viewModel: HomeViewModel,
    onNavigateBack: () -> Unit
) {
    val quest = viewModel.activeQuest.collectAsState().value ?: return

    // --- ESTADOS DE LA SESIÓN ---
    var currentSetIndex by remember { mutableStateOf(0) }
    var isResting by remember { mutableStateOf(false) }
    var restTimer by remember { mutableStateOf(quest.restSeconds) }

    // Valor actual (Reps o Segundos)
    var currentTargetValue by remember { mutableStateOf(quest.sets[currentSetIndex]) }

    // Lógica del Timer (Para descanso o ejercicios por tiempo)
    var isTimerActive by remember { mutableStateOf(false) }

    // --- LÓGICA DEL BUCLE DE TIEMPO (Engine) ---
    LaunchedEffect(isTimerActive, isResting) {
        // Caso A: Ejercicio por Tiempo
        while (isTimerActive && currentTargetValue > 0 && !isResting) {
            delay(1000L)

            // SONIDO: Bip en los últimos 3 segundos
            if (currentTargetValue <= 4 && currentTargetValue > 1) { // 3, 2, 1
                SoundManager.playBeep()
            }
            // SONIDO: Final
            if (currentTargetValue == 1) {
                SoundManager.playSuccess()
            }

            currentTargetValue--
        }

        // Caso B: Tiempo de Descanso
        while (isResting && restTimer > 0) {
            delay(1000L)

            // SONIDO: Bip para avisar que el descanso termina
            if (restTimer <= 4 && restTimer > 1) {
                SoundManager.playBeep()
            }

            restTimer--
            // ... resto del código ...
        }
    }

    // --- UI PRINCIPAL ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground) // Fondo oscuro RPG
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. CABECERA TÁCTICA
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Gray)
            }

            // Badge de XP
            Surface(
                color = XpBadgeGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, XpBadgeGold)
            ) {
                Text(
                    text = "+${quest.xpReward} XP",
                    color = XpBadgeGold,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. INFORMACIÓN DE MISIÓN
        Text(
            text = quest.title.uppercase(),
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            letterSpacing = 1.sp
        )

        Text(
            text = "SERIE ${currentSetIndex + 1} / ${quest.sets.size}",
            color = Color.Gray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. BARRA DE PROGRESO DE SERIES (Visual)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            quest.sets.forEachIndexed { index, _ ->
                // Indicador visual de serie (Cajitas)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .width(if (index == currentSetIndex) 40.dp else 24.dp) // La actual es más ancha
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (index < currentSetIndex) RpgNeonCyan.copy(alpha = 0.3f) // Pasadas
                            else if (index == currentSetIndex) RpgNeonCyan // Actual
                            else Color.DarkGray // Futuras
                        )
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. ZONA CENTRAL (EL REACTOR)
        if (isResting) {
            // --- MODO DESCANSO (ROJO ALERTA) ---
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("RECUPERANDO...", color = Color.Red, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(220.dp)
                        .border(4.dp, Color.Red.copy(alpha = 0.3f), CircleShape)
                        .border(1.dp, Color.Red, CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Text(
                        text = "$restTimer",
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Red
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Saltar Descanso
                Button(
                    onClick = {
                        isResting = false
                        restTimer = quest.restSeconds
                        if (currentSetIndex < quest.sets.size - 1) {
                            currentSetIndex++
                            currentTargetValue = quest.sets[currentSetIndex]
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("SALTAR DESCANSO", color = Color.White)
                }
            }

        } else {
            // --- MODO COMBATE (NEÓN CYAN) ---

            // Círculo Central
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(260.dp)
                    .border(4.dp, RpgNeonCyan.copy(alpha = 0.3f), CircleShape) // Glow externo
                    .border(2.dp, RpgNeonCyan, CircleShape) // Borde neón
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                    .clickable {
                        // Si es Timer, tocar el círculo pausa/reanuda
                        if (quest.type == ExerciseType.TIMER) {
                            isTimerActive = !isTimerActive
                        }
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$currentTargetValue",
                        fontSize = 90.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = (-2).sp
                    )
                    Text(
                        text = if (quest.type == ExerciseType.REPS) "REPS" else "SEGUNDOS",
                        color = RpgNeonCyan,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )

                    // Icono de Pausa/Play si es Timer
                    if (quest.type == ExerciseType.TIMER) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Icon(
                            imageVector = if(isTimerActive) Icons.Default.Close else Icons.Default.PlayArrow, // Iconos simbólicos
                            contentDescription = null,
                            tint = if(isTimerActive) RpgNeonCyan else Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // CONTROLES DE AJUSTE (Solo para Reps)
            if (quest.type == ExerciseType.REPS) {
                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Botón Menos
                    CircularControlButton(
                        icon = Icons.Default.Close, // X para restar (o minus si tienes icono)
                        color = Color.Red,
                        onClick = { if (currentTargetValue > 0) currentTargetValue-- }
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("AJUSTAR", color = Color.Gray, fontSize = 10.sp)
                        Text("OBJETIVO", color = Color.Gray, fontSize = 10.sp)
                    }

                    // Botón Más
                    CircularControlButton(
                        icon = Icons.Default.Add,
                        color = RpgNeonCyan,
                        onClick = { currentTargetValue++ }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // 5. BOTÓN DE ACCIÓN PRINCIPAL
        if (!isResting) {
            val isTimerRunning = (quest.type == ExerciseType.TIMER && isTimerActive)

            Button(
                onClick = {
                    if (isTimerRunning) {
                        // Si el timer corre, el botón pausa
                        isTimerActive = false
                    } else {
                        // LOGICA DE TERMINAR SERIE
                        if (currentSetIndex < quest.sets.size - 1) {
                            // Toca descanso
                            isResting = true
                            isTimerActive = false
                        } else {
                            // FIN DE LA MISIÓN
                            viewModel.completeQuest(quest)
                            onNavigateBack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isTimerRunning) Color.Red else RpgNeonCyan // Rojo para pausar, Cyan para completar
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            isTimerRunning -> "PAUSAR TIEMPO"
                            currentSetIndex == quest.sets.size - 1 -> "¡COMPLETAR MISIÓN!"
                            else -> "SERIE TERMINADA"
                        },
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isTimerRunning) Color.White else Color.Black
                    )

                    if (!isTimerRunning) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// COMPONENTE AUXILIAR PARA BOTONES REDONDOS
@Composable
fun CircularControlButton(
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp).border(1.dp, color.copy(alpha=0.5f), CircleShape),
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
    }
}