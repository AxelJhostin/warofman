package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.ui.components.ActiveChallengeCard
import com.negocio.warofmen.ui.components.CreateChallengeDialog
import com.negocio.warofmen.ui.components.EmptyChallengeCard
import com.negocio.warofmen.ui.components.LevelUpDialog
import com.negocio.warofmen.ui.components.QuestCard
import com.negocio.warofmen.ui.components.XpProgressBar
import com.negocio.warofmen.ui.theme.RpgBackground
import com.negocio.warofmen.ui.theme.RpgPanel
import com.negocio.warofmen.ui.viewmodel.HomeViewModel

@Composable
fun PantallaJuego(
    viewModel: HomeViewModel,
    onStartQuest: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStreak: () -> Unit
) {
    // 1. Observamos los estados
    val gameState by viewModel.gameState.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val showLevelUp by viewModel.showLevelUpDialog.collectAsState()

    // Estado local para mostrar/ocultar el diálogo de "Crear Reto"
    var showChallengeDialog by remember { mutableStateOf(false) }

    // 2. DIÁLOGOS
    // A. Subida de Nivel
    if (showLevelUp) {
        LevelUpDialog(level = gameState.level) {
            viewModel.dismissDialog()
        }
    }

    // B. Crear Nuevo Juramento (Reto)
    if (showChallengeDialog) {
        CreateChallengeDialog(
            onDismiss = { showChallengeDialog = false },
            // CAMBIO: Ahora recibimos 'deadline' en vez de 'days'
            onCreate = { target, deadline, desc ->
                viewModel.createChallenge(target, deadline, desc)
                showChallengeDialog = false
            }
        )
    }

    // 3. Estructura Principal
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
    ) {
        // --- HEADER FIJO (Panel Superior) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RpgPanel)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
        ) {
            // Fila Superior: Datos y Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // A. Nombre y Clase
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = gameState.name.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = gameState.gender.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray,
                        letterSpacing = 2.sp
                    )
                }

                // B. ICONO DE RACHA (STREAK) 🔥
                val streakColor = if (gameState.currentStreak > 0) Color(0xFFFF5722) else Color.Gray

                Surface(
                    color = streakColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, streakColor),
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable { onOpenStreak() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🔥", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${gameState.currentStreak}",
                            color = streakColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // C. Botón Ajustes
                IconButton(onClick = onOpenSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ajustes",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // D. Barra de XP
            XpProgressBar(gameState.currentXp, gameState.maxXp, gameState.level)
        }

        // --- CONTENIDO SCROLLEABLE (Retos + Misiones) ---
        // Usamos LazyColumn para que todo lo de abajo haga scroll junto si la pantalla es chica
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que no se tape con botones flotantes
        ) {
            // ITEM 1: SECCIÓN DE JURAMENTO (RETO)
            item {
                Spacer(modifier = Modifier.height(16.dp))

                if (gameState.activeChallenge != null) {
                    // Si hay reto, mostramos la tarjeta activa (con cronómetro)
                    ActiveChallengeCard(
                        challenge = gameState.activeChallenge!!,
                        currentWeight = gameState.currentWeight
                    )
                } else {
                    // Si no hay reto, mostramos el botón para crear uno
                    EmptyChallengeCard(
                        onClick = { showChallengeDialog = true }
                    )
                }
            }

            // ITEM 2: TÍTULO DE MISIONES
            item {
                Text(
                    text = "ENTRENAMIENTOS DISPONIBLES",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
                )
            }

            // ITEM 3: LISTA DE MISIONES
            items(quests) { quest ->
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    QuestCard(
                        quest = quest,
                        onComplete = {
                            viewModel.selectQuest(quest)
                            onStartQuest()
                        }
                    )
                }
            }
        }
    }
}