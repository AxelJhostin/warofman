package com.negocio.warofmen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.dato.GameStorage
import com.negocio.warofmen.dato.PlayerCharacter
import com.negocio.warofmen.dato.Quest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val storage = GameStorage(application)

    // Estado del Jugador
    private val _gameState = MutableStateFlow(PlayerCharacter())
    val gameState: StateFlow<PlayerCharacter> = _gameState.asStateFlow()

    // Lista de Misiones
    private val _quests = MutableStateFlow<List<Quest>>(listOf())
    val quests: StateFlow<List<Quest>> = _quests.asStateFlow()

    // Controla si mostramos el diálogo de Level Up (Subida de nivel)
    private val _showLevelUpDialog = MutableStateFlow(false)
    val showLevelUpDialog: StateFlow<Boolean> = _showLevelUpDialog.asStateFlow()

    // Controla si estamos viendo las Estadísticas Detalladas
    private val _isViewingStats = MutableStateFlow(false)
    val isViewingStats: StateFlow<Boolean> = _isViewingStats.asStateFlow()

    init {
        // Cargar datos al iniciar la ViewModel
        viewModelScope.launch {
            storage.getUserFlow.collectLatest { savedPlayer ->
                _gameState.value = savedPlayer
                // Cargamos las misiones adecuadas para el nivel guardado
                loadQuests(savedPlayer.level)
            }
        }
    }

    // ------------------------------------------------------
    // LÓGICA 1: CREACIÓN DE PERSONAJE CON CÁLCULO DE STATS
    // ------------------------------------------------------
    fun createCharacter(name: String, weight: Float, height: Float, age: Int, gender: String) {

        // A. Calculamos IMC (Peso / Altura al cuadrado en metros)
        val heightMeters = height / 100
        val bmi = if (heightMeters > 0) weight / (heightMeters * heightMeters) else 0f

        // B. Stats Base (Todos empiezan con 5)
        var baseStr = 5 // Fuerza
        var baseSta = 5 // Resistencia
        var baseAgi = 5 // Agilidad
        var baseWil = 5 // Voluntad
        var baseLuk = 5 // Suerte (Aleatoria, la dejamos fija en 5 al inicio)

        // C. Modificadores por Género (Arquetipos clásicos)
        if (gender == "H") {
            baseStr += 2
            baseSta += 1
        } else { // "M"
            baseAgi += 2
            baseWil += 1
        }

        // D. Modificadores por IMC (Realidad física)
        if (bmi > 25) {
            baseStr += 3 // Mayor masa suele implicar mayor fuerza absoluta inicial
            baseAgi -= 2 // Mayor peso dificulta la agilidad
        } else if (bmi < 20) {
            baseAgi += 3 // Cuerpo ligero, mayor agilidad
            baseStr -= 1 // Menos masa muscular base
        }

        // E. Modificadores por Edad
        if (age < 25) {
            baseSta += 2 // Energía de la juventud
            baseWil -= 1 // Menos disciplina mental
        } else if (age > 35) {
            baseWil += 3 // Mayor disciplina y fuerza mental
            baseAgi -= 1 // El cuerpo se vuelve un poco más rígido
        }

        // Aseguramos que ninguna stat empiece en negativo o cero
        baseStr = baseStr.coerceAtLeast(1)
        baseSta = baseSta.coerceAtLeast(1)
        baseAgi = baseAgi.coerceAtLeast(1)
        baseWil = baseWil.coerceAtLeast(1)
        val initialHistory = listOf("${System.currentTimeMillis()}:$weight")
        // F. Creamos el objeto Jugador
        val newPlayer = PlayerCharacter(
            name = name,
            strength = baseStr,
            stamina = baseSta,
            agility = baseAgi,
            willpower = baseWil,
            luck = baseLuk,
            gender = if (gender == "H") "Guerrero" else "Amazona",
            isCreated = true,
            age = age,
            weight = weight,
            height = height,
            bmi = bmi,
            weightHistory = initialHistory
        )

        saveAndUpdate(newPlayer)
    }

    // ------------------------------------------------------
    // LÓGICA 2: COMPLETAR MISIÓN Y SUBIR DE NIVEL
    // ------------------------------------------------------
    fun completeQuest(quest: Quest) {
        val currentPlayer = _gameState.value

        // Variables temporales para calcular nuevos valores
        var newXp = currentPlayer.currentXp + quest.xpReward
        var newLevel = currentPlayer.level
        var newMaxXp = currentPlayer.maxXp

        // Obtenemos stats actuales
        var newStr = currentPlayer.strength
        var newSta = currentPlayer.stamina
        var newAgi = currentPlayer.agility
        var newWil = currentPlayer.willpower
        val newLuk = currentPlayer.luck // La suerte no suele subir entrenando

        // 1. Aplicamos la mejora específica de la Misión (Entrenar fuerza sube fuerza)
        when (quest.statBonus) {
            "STR" -> newStr += 1
            "STA" -> newSta += 1
            "AGI" -> newAgi += 1
            "WIL" -> newWil += 1
        }

        // 2. Comprobamos si sube de nivel
        var leveledUp = false
        if (newXp >= newMaxXp) {
            newXp -= newMaxXp // Restamos el coste del nivel (XP sobrante)
            newLevel += 1
            newMaxXp = (newMaxXp * 1.2).toInt() // El siguiente nivel es 20% más difícil

            // BONUS DE LEVEL UP: Suben todas las estadísticas un poco (Mejora general)
            newStr += 1
            newSta += 1
            newAgi += 1
            newWil += 1

            leveledUp = true
        }

        // 3. Creamos la copia actualizada del jugador
        val updatedPlayer = currentPlayer.copy(
            currentXp = newXp,
            level = newLevel,
            maxXp = newMaxXp,
            strength = newStr,
            stamina = newSta,
            agility = newAgi,
            willpower = newWil,
            luck = newLuk
        )

        // 4. Guardamos en DB y actualizamos UI
        saveAndUpdate(updatedPlayer)

        // 5. Acciones post-guardado
        if (leveledUp) {
            _showLevelUpDialog.value = true
            loadQuests(newLevel) // Regeneramos misiones con nueva dificultad
        }
    }

    // ------------------------------------------------------
    // UTILIDADES Y NAVEGACIÓN
    // ------------------------------------------------------

    // Cambia entre pantalla de juego y estadísticas
    fun toggleStatsView() {
        _isViewingStats.value = !_isViewingStats.value
    }

    // Cierra el pop-up de nivel subido
    fun dismissLevelUpDialog() {
        _showLevelUpDialog.value = false
    }

    // Helper para guardar en DataStore y actualizar StateFlow
    private fun saveAndUpdate(player: PlayerCharacter) {
        _gameState.value = player
        viewModelScope.launch { storage.savePlayer(player) }
    }

    // Generador de misiones basado en nivel (Ahora incluye AGI y WIL)
    private fun loadQuests(level: Int) {
        val m = level // Multiplicador simple

        _quests.value = listOf(
            // Misión de Fuerza
            Quest(1, "Flexiones Espartanas", "Haz ${5 * m} flexiones de pecho", 20 * level, "STR"),

            // Misión de Resistencia
            Quest(2, "Sentadillas Infinitas", "Haz ${10 * m} sentadillas", 15 * level, "STA"),

            // Misión de Fuerza (Tiempo)
            Quest(3, "Plancha de Acero", "Aguanta ${10 * m} segundos en plancha", 25 * level, "STR"),

            // Misión de Agilidad (Nueva)
            Quest(4, "Burpees Explosivos", "Haz ${3 * m} burpees rápidos", 30 * level, "AGI"),

            // Misión de Voluntad (Nueva - Mental/Isométrico)
            Quest(5, "Meditación de Guerrero", "Siéntate en la pared (Wall sit) ${15 * m} segundos", 20 * level, "WIL")
        )
    }

    // Función para registrar un nuevo peso en el historial
    fun updateWeight(newWeight: Float) {
        val currentPlayer = _gameState.value

        // 1. Recalcular IMC
        val heightMeters = currentPlayer.height / 100
        val newBmi = if (heightMeters > 0) newWeight / (heightMeters * heightMeters) else 0f

        // 2. Crear registro histórico "TIEMPO:PESO"
        val currentTime = System.currentTimeMillis()
        val historyEntry = "$currentTime:$newWeight"

        // 3. Actualizar lista (Añadimos el nuevo y ordenamos por si acaso)
        val newHistory = currentPlayer.weightHistory.toMutableList()
        newHistory.add(historyEntry)

        // 4. Guardar todo
        val updatedPlayer = currentPlayer.copy(
            weight = newWeight,
            bmi = newBmi,
            weightHistory = newHistory
        )

        saveAndUpdate(updatedPlayer)
    }
}