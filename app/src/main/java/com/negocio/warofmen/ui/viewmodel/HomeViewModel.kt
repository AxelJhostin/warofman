package com.negocio.warofmen.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.data.repository.GameRepository
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.data.model.Quest
import com.negocio.warofmen.data.source.QuestProvider
import com.negocio.warofmen.core.util.GameUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val _activeQuest = MutableStateFlow<Quest?>(null)
    val activeQuest: StateFlow<Quest?> = _activeQuest.asStateFlow()

    fun selectQuest(quest: Quest) {
        _activeQuest.value = quest
    }

    fun clearActiveQuest() {
        _activeQuest.value = null
    }

    private val repository = GameRepository(application)

    // Estados
    private val _gameState = MutableStateFlow(PlayerCharacter())
    val gameState: StateFlow<PlayerCharacter> = _gameState.asStateFlow()

    private val _quests = MutableStateFlow<List<Quest>>(listOf())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    private val _showLevelUpDialog = MutableStateFlow(false)
    val showLevelUpDialog: StateFlow<Boolean> = _showLevelUpDialog.asStateFlow()

    init {
        viewModelScope.launch {
            repository.playerFlow.collectLatest { savedPlayer ->
                _gameState.value = savedPlayer
                loadQuests(savedPlayer.level)
            }
        }
    }

    fun completeQuest(quest: Quest) {

        val context = getApplication<Application>().applicationContext
        val currentPlayer = _gameState.value

        // 1. Solo sumamos XP (Ya no stats inmediatos)
        var newXp = currentPlayer.currentXp + quest.xpReward
        var newLevel = currentPlayer.level
        var newMaxXp = currentPlayer.maxXp

        // Stats actuales (se mantienen igual por ahora)
        var newStr = currentPlayer.strength
        var newSta = currentPlayer.stamina
        var newAgi = currentPlayer.agility
        var newWil = currentPlayer.willpower
        var newLuk = currentPlayer.luck

        val totalVolume = quest.sets.sum()
        val logEntry = "${quest.id}:${System.currentTimeMillis()}:$totalVolume"
        val newWorkoutLogs = currentPlayer.workoutLogs.toMutableList()
        newWorkoutLogs.add(logEntry)

        // 2. Comprobamos si sube de nivel (AQUÍ es donde suben los stats)
        var leveledUp = false
        if (newXp >= newMaxXp) {
            newXp -= newMaxXp
            newLevel += 1
            newMaxXp = (newMaxXp * 1.2).toInt()

            // BONUS DE LEVEL UP: ¡Aquí está el premio gordo!
            // Subimos +1 a todo (Representa que tu cuerpo mejora en general)
            newStr += 1
            newSta += 1
            newAgi += 1
            newWil += 1
            // La suerte la dejamos igual o la subes si quieres

            leveledUp = true
        }

        // 3. Guardamos
        val updatedPlayer = currentPlayer.copy(
            currentXp = newXp,
            level = newLevel,
            maxXp = newMaxXp,
            strength = newStr,
            stamina = newSta,
            agility = newAgi,
            willpower = newWil,
            luck = newLuk,
            workoutLogs = newWorkoutLogs
        )

        updatePlayer(updatedPlayer)

        if (leveledUp) {
            GameUtils.vibrate(context, "levelup") // Vibración épica
            _showLevelUpDialog.value = true
            loadQuests(newLevel)
        } else {
            GameUtils.vibrate(context, "success") // Vibración normal
        }
    }

    fun updateWeight(newWeight: Float) {
        val current = _gameState.value
        val hM = current.height / 100
        val newBmi = if (hM > 0) newWeight / (hM * hM) else 0f
        val newHistory = current.weightHistory.toMutableList().apply { add("${System.currentTimeMillis()}:$newWeight") }

        updatePlayer(current.copy(weight = newWeight, bmi = newBmi, weightHistory = newHistory))
    }

    fun dismissDialog() { _showLevelUpDialog.value = false }

    private fun updatePlayer(player: PlayerCharacter) {
        viewModelScope.launch { repository.savePlayer(player) }
    }

    private fun loadQuests(level: Int) {
        _quests.value = QuestProvider.getQuestsForLevel(level)
    }
}