package com.negocio.warofmen.dato

data class PlayerCharacter(
    val name: String = "",
    val level: Int = 1,
    val currentXp: Int = 0,
    val maxXp: Int = 100,

    // --- STATS PRINCIPALES ---
    val strength: Int = 5,    // Fuerza
    val stamina: Int = 5,     // Resistencia
    val agility: Int = 5,     // Agilidad (NUEVO)
    val willpower: Int = 5,   // Voluntad/Disciplina (NUEVO)
    val luck: Int = 5,        // Suerte (NUEVO)

    val gender: String = "Guerrero",
    val isCreated: Boolean = false,

    // Datos Reales
    val age: Int = 25,
    val weight: Float = 70f,
    val height: Float = 170f,
    val bmi: Float = 24.2f,

    val weightHistory: List<String> = emptyList()
)

data class Quest(
    val id: Int,
    val title: String,
    val description: String,
    val xpReward: Int,
    val statBonus: String, // "STR", "AGI", "STA", "WIL", "LUK"
    val difficultyMultiplier: Float = 1.0f
)