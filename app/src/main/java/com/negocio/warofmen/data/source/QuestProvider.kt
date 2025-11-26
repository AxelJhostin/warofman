package com.negocio.warofmen.data.source

import com.negocio.warofmen.data.model.ExerciseFamily
import com.negocio.warofmen.data.model.ExerciseType
import com.negocio.warofmen.data.model.Quest
import kotlin.math.min

object QuestProvider {

    /**
     * Genera misiones dinámicamente basadas en el Nivel del Jugador.
     * Usa lógica de "Dientes de Sierra": La dificultad técnica sube cada 20 niveles,
     * pero las repeticiones bajan para reiniciar la progresión.
     */
    fun getQuestsForLevel(level: Int): List<Quest> {
        return listOf(
            generateQuest(1, ExerciseDefinitions.pushFamily, level),
            generateQuest(2, ExerciseDefinitions.legFamily, level),
            generateQuest(3, ExerciseDefinitions.coreFamily, level),
            generateQuest(4, ExerciseDefinitions.backFamily, level)  // Espalda/Cardio
        )
    }

    private fun generateQuest(id: Int, family: ExerciseFamily, level: Int): Quest {
        // 1. Calcular el Tier (Rango de habilidad 0-4)
        // Cada 20 niveles cambiamos de ejercicio
        val tierIndex = (level - 1) / 20

        // Aseguramos no pasarnos del límite de la lista (si es nivel 200, se queda en el último)
        val safeTierIndex = min(tierIndex, family.variants.lastIndex)

        // Obtenemos el ejercicio correspondiente (Ej: Nivel 25 -> Flexiones Clásicas)
        val variant = family.variants[safeTierIndex]

        // 2. Calcular Progresión dentro del Tier (0-19)
        // Esto hace el efecto "Diente de Sierra". Al nivel 21, esto vuelve a ser 0.
        val progressionInTier = (level - 1) % 20

        // 3. Calcular Repeticiones o Tiempo
        val baseAmount = if (variant.isTimer) 15 else 8 // Empezamos con 8 reps o 15 seg
        val increment = if (variant.isTimer) 5 else 1   // Sumamos 1 rep o 5 seg por nivel

        val targetAmount = baseAmount + (progressionInTier * increment)

        // 4. Calcular XP (La recompensa siempre sube, nunca baja)
        // Base * Nivel * Multiplicador de Dificultad del ejercicio
        val xp = (20 * level * variant.baseDifficulty).toInt()

        // 5. Construir la Misión
        val unit = if (variant.isTimer) "segundos" else "reps"
        val type = if (variant.isTimer) ExerciseType.TIMER else ExerciseType.REPS

        // Series: Hacemos 3 o 4 series dependiendo de la dificultad
        val setsCount = 3 + (progressionInTier / 10) // Empieza con 3 series, sube a 4 al final del tier
        val setsList = List(setsCount) { targetAmount }

        return Quest(
            id = id,
            title = variant.name.uppercase(),
            description = "Realiza $setsCount series de $targetAmount $unit.",
            xpReward = xp,
            statBonus = family.stat,
            type = type,
            sets = setsList,
            restSeconds = if (variant.isTimer) 60 else 45
        )
    }
}