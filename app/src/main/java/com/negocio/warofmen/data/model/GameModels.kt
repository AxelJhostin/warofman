package com.negocio.warofmen.data.model

// Enum para tipos de items
enum class ItemType { WEAPON, ARMOR, ACCESSORY }

// Enum para tipos de ejercicio
enum class ExerciseType { REPS, TIMER }

// --- NUEVO: REGISTRO CORPORAL DETALLADO ---
// Esto guarda la "foto" de tu cuerpo en un momento dado
data class BodyLog(
    val timestamp: Long,

    // 1. Obligatorio (Base)
    val weight: Float,

    // 2. Opcionales para Fórmula de Grasa (Navy Method)
    val neck: Float? = null,   // Cuello
    val waist: Float? = null,  // Cintura (obligatorio para grasa)
    val hip: Float? = null,    // Cadera (obligatorio para mujeres)

    // 3. Opcionales Estéticos (Para ver progreso visual)
    val chest: Float? = null,  // Pecho
    val biceps: Float? = null, // Brazos
    val thigh: Float? = null,  // Muslo

    // 4. Datos Calculados
    val bodyFatPercentage: Float? = null, // % Grasa
    val leanMass: Float? = null           // Masa magra
)

// --- JUGADOR ACTUALIZADO ---
data class PlayerCharacter(
    // Identidad

    val name: String = "",
    val gender: String = "Guerrero", // "Guerrero" (H) o "Amazona" (M)
    val age: Int = 25,
    val height: Float = 170f,
    val isCreated: Boolean = false,

    // Estado Actual (Snapshots para acceso rápido en la UI)
    val currentWeight: Float = 70f,
    val currentBmi: Float = 24.2f,
    val currentBodyFat: Float? = null, // Nuevo: Grasa actual

    // Stats RPG
    val level: Int = 1,
    val currentXp: Int = 0,
    val maxXp: Int = 100,
    val strength: Int = 5,
    val stamina: Int = 5,
    val agility: Int = 5,
    val willpower: Int = 5,
    val luck: Int = 5,

    // Historiales y Listas
    // AQUÍ EL CAMBIO: Usamos listas de objetos complejos
    val measurementLogs: List<BodyLog> = emptyList(),

    // Estos los mantenemos simples por ahora (String)
    val workoutLogs: List<String> = emptyList(),
    val inventory: List<String> = emptyList(),
    val currentStreak: Int = 0,      // Días seguidos (Ej: 5)
    val lastWorkoutDate: Long = 0L
)

// Clase Item (Objeto del juego)
data class GameItem(
    val id: Int,
    val name: String,
    val description: String,
    val type: ItemType,
    val bonusStat: String,
    val bonusValue: Int,
    val price: Int = 0
)

// Clase Misión
data class Quest(
    val id: Int,
    val title: String,
    val description: String,
    val xpReward: Int,
    val statBonus: String,
    val type: ExerciseType = ExerciseType.REPS,
    val sets: List<Int> = listOf(10),
    val restSeconds: Int = 60
)

data class ExerciseVariant(
    val name: String,       // Ej: "Flexiones Diamante"
    val baseDifficulty: Float, // Ej: 1.5x más XP
    val isTimer: Boolean = false // Si es por tiempo o reps
)

data class ExerciseFamily(
    val id: String,         // Ej: "push_family"
    val stat: String,       // Ej: "STR"
    val variants: List<ExerciseVariant> // Lista de 5 niveles (Novato -> Leyenda)
)