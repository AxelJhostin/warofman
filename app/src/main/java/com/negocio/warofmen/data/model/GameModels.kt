package com.negocio.warofmen.data.model

enum class ExerciseType { REPS, TIMER }

data class PlayerCharacter(
    val name: String = "",
    val level: Int = 1,
    val currentXp: Int = 0,
    val maxXp: Int = 100,
    val strength: Int = 5,    // Fuerza
    val stamina: Int = 5,     // Resistencia
    val agility: Int = 5,     // Agilidad (NUEVO)
    val willpower: Int = 5,   // Voluntad/Disciplina (NUEVO)
    val luck: Int = 5,        // Suerte (NUEVO)
    val gender: String = "Guerrero",
    val isCreated: Boolean = false,
    val age: Int = 25,
    val weight: Float = 70f,
    val height: Float = 170f,
    val bmi: Float = 24.2f,
    val weightHistory: List<String> = emptyList(),
    val workoutLogs: List<String> = emptyList()
)

data class Quest(
    val id: Int,
    val title: String,
    val description: String,
    val xpReward: Int,
    val statBonus: String,

    val type: ExerciseType = ExerciseType.REPS, // ¿Es de contar o de tiempo?
    val sets: List<Int> = listOf(10), // Lista de series (ej: [10, 10, 10] o [30, 30] segundos)
    val restSeconds: Int = 60 // Tiempo de descanso entre series
)