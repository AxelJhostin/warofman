package com.negocio.warofmen.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.data.model.Challenge
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.data.model.Quest
import com.negocio.warofmen.data.model.WorkoutLog // <--- NUEVO IMPORT
import com.negocio.warofmen.data.repository.GameRepository
import com.negocio.warofmen.data.source.QuestProvider
import com.negocio.warofmen.data.source.MilestoneProvider
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

        // Fecha actual (momento exacto en que termina la misión)
        val now = System.currentTimeMillis()

        // ---------------------------------------------------------
        // 1. LÓGICA DE RACHA (STREAK)
        // ---------------------------------------------------------
        var newStreak = currentPlayer.currentStreak
        val lastDate = currentPlayer.lastWorkoutDate

        if (GameUtils.isToday(lastDate)) {
            // Ya entrenó hoy: La racha no cambia (ni sube ni baja)
        } else if (GameUtils.isYesterday(lastDate) || lastDate == 0L) {
            // Entrenó ayer O es su primer entrenamiento histórico: Sube la racha
            newStreak += 1
        } else {
            // Se saltó uno o más días: La racha se reinicia a 1
            newStreak = 1
        }

        // ---------------------------------------------------------
        // 2. CÁLCULO DE XP CON BONUS (SISTEMA DE RECOMPENSAS)
        // ---------------------------------------------------------
        // Consultamos qué multiplicador corresponde a la NUEVA racha
        val multiplier = MilestoneProvider.getCurrentMultiplier(newStreak)

        // XP Base de la misión
        val baseXp = quest.xpReward

        // Calculamos la XP final aplicando el bonus (convertimos a Int)
        val xpWithBonus = (baseXp * multiplier).toInt()

        // ---------------------------------------------------------
        // 3. LÓGICA DE NIVEL Y ATRIBUTOS
        // ---------------------------------------------------------
        var newXp = currentPlayer.currentXp + xpWithBonus
        var newLevel = currentPlayer.level
        var newMaxXp = currentPlayer.maxXp

        // Stats actuales
        var newStr = currentPlayer.strength
        var newSta = currentPlayer.stamina
        var newAgi = currentPlayer.agility
        var newWil = currentPlayer.willpower
        var newLuk = currentPlayer.luck

        var leveledUp = false

        // Bucle while por si gana tanta XP que sube 2 niveles de golpe
        while (newXp >= newMaxXp) {
            newXp -= newMaxXp // Restamos el costo del nivel
            newLevel += 1
            newMaxXp = (newMaxXp * 1.2).toInt() // El siguiente nivel es 20% más difícil

            // BONUS DE LEVEL UP: Suben todas las estadísticas +1
            newStr += 1
            newSta += 1
            newAgi += 1
            newWil += 1
            // La suerte (LUK) la dejamos igual por ahora

            leveledUp = true
        }

        // ---------------------------------------------------------
        // 4. REGISTRO DE HISTORIAL (LOGS)
        // ---------------------------------------------------------
        val totalVolume = quest.sets.sum() // Suma total de reps o segundos

        // CAMBIO AQUÍ: Creamos el objeto WorkoutLog en lugar del String antiguo
        val newLog = WorkoutLog(
            timestamp = now,
            questId = quest.id,
            questTitle = quest.title,
            totalVolume = totalVolume,
            xpEarned = xpWithBonus,
            multiplier = multiplier
        )

        // Agregamos al inicio de la lista (para que el más reciente salga primero)
        val newWorkoutLogs = currentPlayer.workoutLogs.toMutableList()
        newWorkoutLogs.add(0, newLog)

        // ---------------------------------------------------------
        // 5. FEEDBACK SENSORIAL (VIBRACIÓN Y DIÁLOGOS)
        // ---------------------------------------------------------
        if (leveledUp) {
            GameUtils.vibrate(context, "levelup") // Vibración épica
            _showLevelUpDialog.value = true       // Mostrar popup
            loadQuests(newLevel)                  // Regenerar misiones más difíciles
        } else {
            GameUtils.vibrate(context, "success") // Vibración normal
        }

        // ---------------------------------------------------------
        // 6. GUARDADO FINAL (ACTUALIZAR JUGADOR)
        // ---------------------------------------------------------
        val updatedPlayer = currentPlayer.copy(
            // Progreso
            currentXp = newXp,
            level = newLevel,
            maxXp = newMaxXp,

            // Atributos
            strength = newStr,
            stamina = newSta,
            agility = newAgi,
            willpower = newWil,
            luck = newLuk,

            // Historiales y Racha
            workoutLogs = newWorkoutLogs, // Lista actualizada con objetos
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
        var challenge = currentPlayer.activeChallenge
        var xpReward = 0
        var challengeCompleted = false

        if (challenge != null && !challenge.isCompleted && !challenge.isFailed) {
            // Verificar si cumplió (Usamos la misma lógica: bajar o subir)
            val isSuccess = if (challenge.startWeight > challenge.targetWeight) {
                newWeight <= challenge.targetWeight // Bajar: Peso actual menor o igual al target
            } else {
                newWeight >= challenge.targetWeight // Subir: Peso actual mayor o igual
            }

            if (isSuccess) {
                challenge = challenge.copy(isCompleted = true)
                xpReward = challenge.rewardXp
                challengeCompleted = true
            }
        }


        // 4. Actualizar al jugador
        val updatedPlayer = currentPlayer.copy(
            currentWeight = newWeight,
            currentBmi = newBmi,
            measurementLogs = newHistory,
            activeChallenge = challenge,
            currentXp = currentPlayer.currentXp + xpReward // Sumamos XP si ganó
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

    fun createChallenge(targetWeight: Float, deadline: Long, description: String) {
        val currentPlayer = _gameState.value
        val now = System.currentTimeMillis()

        // Calculamos la duración aproximada solo para dar XP extra (opcional)
        val diff = deadline - now
        val days = (diff / (1000 * 60 * 60 * 24)).toInt()
        val bonusXp = 1000 + (days * 10) // 10 XP extra por día de duración

        val newChallenge = Challenge(
            targetWeight = targetWeight,
            startWeight = currentPlayer.currentWeight,
            startDate = now,
            deadline = deadline, // Usamos la fecha directa del calendario
            description = description,
            rewardXp = bonusXp
        )

        // Guardamos
        val updatedPlayer = currentPlayer.copy(activeChallenge = newChallenge)
        updatePlayer(updatedPlayer)
    }

    fun cancelChallenge() {
        val currentPlayer = _gameState.value
        // Al ponerlo en null, el Storage lo borrará automáticamente
        val updatedPlayer = currentPlayer.copy(activeChallenge = null)
        updatePlayer(updatedPlayer)
    }
}