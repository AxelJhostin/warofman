package com.negocio.warofmen.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.data.model.Quest
import com.negocio.warofmen.data.repository.GameRepository
import com.negocio.warofmen.data.source.QuestProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    // Bandera para saber si ya leímos el DataStore (Pantalla de Carga)
    private val _isDataLoaded = MutableStateFlow(false)
    val isDataLoaded: StateFlow<Boolean> = _isDataLoaded.asStateFlow()

    // Estado del Jugador
    private val _gameState = MutableStateFlow(PlayerCharacter())
    val gameState: StateFlow<PlayerCharacter> = _gameState.asStateFlow()

    // Lista de Misiones Disponibles
    private val _quests = MutableStateFlow<List<Quest>>(listOf())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    // Misión seleccionada para entrenar (Pantalla Naranja)
    private val _activeQuest = MutableStateFlow<Quest?>(null)
    val activeQuest: StateFlow<Quest?> = _activeQuest.asStateFlow()

    // Control del Diálogo de Level Up
    private val _showLevelUpDialog = MutableStateFlow(false)
    val showLevelUpDialog: StateFlow<Boolean> = _showLevelUpDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.playerFlow.collectLatest { savedPlayer ->
                _gameState.value = savedPlayer
                // Cargamos las misiones adecuadas para el nivel del jugador
                loadQuests(savedPlayer.level)

                // Avisamos a la UI que los datos están listos
                _isDataLoaded.value = true
            }
        }
    }

    // ------------------------------------------------------
    // LÓGICA DE ENTRENAMIENTO Y MISIONES
    // ------------------------------------------------------

    fun selectQuest(quest: Quest) {
        _activeQuest.value = quest
    }

    fun clearActiveQuest() {
        _activeQuest.value = null
    }

    fun completeQuest(quest: Quest) {
        val currentPlayer = _gameState.value
        val context = getApplication<Application>().applicationContext

        // Obtenemos la fecha actual
        val now = System.currentTimeMillis()

        // 1. Cálculo de XP
        var newXp = currentPlayer.currentXp + quest.xpReward
        var newLevel = currentPlayer.level
        var newMaxXp = currentPlayer.maxXp

        // Stats actuales
        var newStr = currentPlayer.strength
        var newSta = currentPlayer.stamina
        var newAgi = currentPlayer.agility
        var newWil = currentPlayer.willpower
        var newLuk = currentPlayer.luck

        // 2. Comprobamos si sube de nivel
        var leveledUp = false
        if (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel += 1
            newMaxXp = (newMaxXp * 1.2).toInt()

            // BONUS DE LEVEL UP: Suben todas las estadísticas +1
            newStr += 1
            newSta += 1
            newAgi += 1
            newWil += 1
            // La suerte la dejamos igual o la subes si quieres

            leveledUp = true
        }

        // 3. Registrar en el Historial de Entrenamientos (LOGS)
        val totalVolume = quest.sets.sum()
        // Formato: "QuestID:Timestamp:Volumen"
        val logEntry = "${quest.id}:$now:$totalVolume"

        val newWorkoutLogs = currentPlayer.workoutLogs.toMutableList()
        newWorkoutLogs.add(logEntry)

        // 4. LÓGICA DE RACHA (STREAK) - ¡NUEVO!
        var newStreak = currentPlayer.currentStreak
        val lastDate = currentPlayer.lastWorkoutDate

        if (GameUtils.isToday(lastDate)) {
            // Ya entrenó hoy: No cambia la racha
        } else if (GameUtils.isYesterday(lastDate) || lastDate == 0L) {
            // Entrenó ayer O es la primera vez en su vida: Sube la racha
            newStreak += 1
        } else {
            // Se saltó un día o más: La racha se reinicia a 1 (hoy cuenta como el primero)
            newStreak = 1
        }

        // 5. Feedback Sensorial (Vibración)
        if (leveledUp) {
            GameUtils.vibrate(context, "levelup")
            _showLevelUpDialog.value = true
            loadQuests(newLevel)
        } else {
            GameUtils.vibrate(context, "success")
        }

        // 6. Guardar cambios
        val updatedPlayer = currentPlayer.copy(
            currentXp = newXp,
            level = newLevel,
            maxXp = newMaxXp,
            strength = newStr,
            stamina = newSta,
            agility = newAgi,
            willpower = newWil,
            luck = newLuk,
            workoutLogs = newWorkoutLogs,
            // Guardamos los datos de racha
            currentStreak = newStreak,
            lastWorkoutDate = now
        )

        updatePlayer(updatedPlayer)
    }

    // ------------------------------------------------------
    // LÓGICA DE PESO Y BIOMETRÍA
    // ------------------------------------------------------

    fun updateWeight(newWeight: Float) {
        val currentPlayer = _gameState.value

        // 1. Recalcular IMC con el nuevo peso
        val newBmi = GameUtils.calculateBMI(newWeight, currentPlayer.height)

        // 2. Crear registro histórico complejo (BodyLog)
        val newLog = BodyLog(
            timestamp = System.currentTimeMillis(),
            weight = newWeight,
            neck = null,
            waist = null,
            hip = null,
            bodyFatPercentage = null
        )

        // 3. Actualizar la lista de registros
        val newHistory = currentPlayer.measurementLogs.toMutableList()
        newHistory.add(newLog)

        // 4. Actualizar al jugador
        val updatedPlayer = currentPlayer.copy(
            currentWeight = newWeight,
            currentBmi = newBmi,
            measurementLogs = newHistory
        )

        updatePlayer(updatedPlayer)
    }

    // ------------------------------------------------------
    // UTILIDADES
    // ------------------------------------------------------

    fun dismissDialog() {
        _showLevelUpDialog.value = false
    }

    private fun updatePlayer(player: PlayerCharacter) {
        viewModelScope.launch {
            repository.savePlayer(player)
        }
    }

    private fun loadQuests(level: Int) {
        _quests.value = QuestProvider.getQuestsForLevel(level)
    }
}