package com.negocio.warofmen.vista

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.componentes.LevelUpDialog
import com.negocio.warofmen.componentes.QuestCard
import com.negocio.warofmen.componentes.XpProgressBar
import com.negocio.warofmen.ui.theme.RpgBackground
import com.negocio.warofmen.ui.theme.RpgPanel
import com.negocio.warofmen.viewmodel.HomeViewModel

@Composable
fun PantallaJuego(
    viewModel: HomeViewModel,
    onStartQuest: () -> Unit // Parámetro nuevo para navegar al entrenamiento
) {
    // Observamos los datos
    val gameState by viewModel.gameState.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val showLevelUp by viewModel.showLevelUpDialog.collectAsState()

    // Diálogo de Nivel Subido
    if (showLevelUp) {
        LevelUpDialog(level = gameState.level) {
            viewModel.dismissDialog()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground) // Fondo oscuro global
    ) {
        // 1. HEADER (Panel de Estado del Personaje)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(RpgPanel) // Fondo del panel superior
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp)
        ) {
            // Fila: Nombre y Clase
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column {
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
                // Aquí podrías poner el Nivel en grande si quisieras
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Barra de XP visual
            XpProgressBar(gameState.currentXp, gameState.maxXp, gameState.level)
        }

        // 2. LISTA DE MISIONES (ENTRENAMIENTOS)
        Text(
            text = "ENTRENAMIENTOS DISPONIBLES",
            style = MaterialTheme.typography.labelLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio extra abajo para que el botón flotante no tape la última tarjeta
        ) {
            items(quests) { quest ->
                // Usamos la QuestCard que ya diseñamos
                QuestCard(
                    quest = quest,
                    onComplete = {
                        // AQUÍ ESTÁ LA MAGIA:
                        // En lugar de "completar" ya, seleccionamos la quest y vamos a la pantalla naranja
                        viewModel.selectQuest(quest)
                        onStartQuest()
                    }
                )
            }
        }
    }
}