package com.negocio.warofmen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.dato.GameRepository
import com.negocio.warofmen.dato.PlayerCharacter
import com.negocio.warofmen.dato.Quest
import com.negocio.warofmen.dato.QuestProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {

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
        val current = _gameState.value
        // Lógica de XP y Stats (Resumida para el ejemplo, usa la lógica completa anterior)
        var newXp = current.currentXp + quest.xpReward
        var newLvl = current.level
        var max = current.maxXp

        // ... (Aplica aquí tu lógica de stats Str, Sta, Agi, Wil, Luk igual que antes) ...
        // NOTA: Copia la lógica exacta de los "when(quest.statBonus)" que hicimos antes.

        // Simulación rápida para no llenar pantalla:
        val newStr = if(quest.statBonus=="STR") current.strength+1 else current.strength
        // ... resto de stats ...

        var leveledUp = false
        if (newXp >= max) {
            newXp -= max; newLvl += 1; max = (max * 1.2).toInt()
            leveledUp = true
        }

        val updated = current.copy(level = newLvl, currentXp = newXp, maxXp = max, strength = newStr) // Agrega las demás

        updatePlayer(updated)
        if (leveledUp) {
            _showLevelUpDialog.value = true
            loadQuests(newLvl)
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