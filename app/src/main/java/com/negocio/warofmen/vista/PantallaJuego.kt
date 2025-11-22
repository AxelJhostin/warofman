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
import com.negocio.warofmen.componentes.LevelUpDialog // Importante: Importar el nuevo componente
import com.negocio.warofmen.componentes.QuestCard
import com.negocio.warofmen.componentes.XpProgressBar
import com.negocio.warofmen.viewmodel.HomeViewModel

@Composable
fun PantallaJuego(viewModel: HomeViewModel) {
    // 1. Observamos los estados del ViewModel
    val gameState by viewModel.gameState.collectAsState()
    val quests by viewModel.quests.collectAsState()
    val showLevelUp by viewModel.showLevelUpDialog.collectAsState() // Nuevo observador

    // 2. Lógica del Diálogo (Se sobrepone a la UI cuando es true)
    if (showLevelUp) {
        LevelUpDialog(level = gameState.level) {
            viewModel.dismissDialog()
        }
    }

    // 3. UI Principal
    Column(modifier = Modifier.fillMaxSize()) {
        // CABECERA DE ESTADISTICAS (STATS)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(16.dp)
        ) {
            Text(text = gameState.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = "Clase: ${gameState.gender}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                StatBadge("FUERZA", gameState.strength)
                StatBadge("RESISTENCIA", gameState.stamina)
            }

            Spacer(modifier = Modifier.height(12.dp))
            XpProgressBar(gameState.currentXp, gameState.maxXp, gameState.level)
        }

        // LISTA DE MISIONES
        Text(
            text = "Misiones Disponibles",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier.padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(quests) { quest ->
                QuestCard(quest = quest, onComplete = {
                    viewModel.completeQuest(quest)
                })
            }
        }
    }
}

@Composable
fun StatBadge(name: String, value: Int) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(text = value.toString(), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(text = name, style = MaterialTheme.typography.labelSmall)
    }
}