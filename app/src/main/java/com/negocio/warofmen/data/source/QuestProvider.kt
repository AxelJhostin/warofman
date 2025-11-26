package com.negocio.warofmen.data.source

import com.negocio.warofmen.data.model.ExerciseType
import com.negocio.warofmen.data.model.Quest

object QuestProvider {

    fun getQuestsForLevel(level: Int): List<Quest> {
        val m = level
        val xpBase = 20 * level

        return listOf(
            // 1. Flexiones (Series piramidales)
            Quest(
                id = 1,
                title = "FLEXIONES DE ACERO",
                description = "Empuja el suelo hasta que tiemble.",
                xpReward = xpBase,
                statBonus = "STR",
                type = ExerciseType.REPS,
                sets = listOf(5 * m, 6 * m, 5 * m, 4 * m), // 4 Series
                restSeconds = 30
            ),

            // 2. Sentadillas (Volumen alto)
            Quest(
                id = 2,
                title = "PIERNAS DE TITÁN",
                description = "Sentadillas profundas.",
                xpReward = (xpBase * 0.8).toInt(),
                statBonus = "STA",
                type = ExerciseType.REPS,
                sets = listOf(10 * m, 10 * m, 10 * m), // 3 Series
                restSeconds = 45
            ),

            // 3. Plancha (Tiempo)
            Quest(
                id = 3,
                title = "PLANCHA ISOMÉTRICA",
                description = "Mantén la posición y respira.",
                xpReward = (xpBase * 1.2).toInt(),
                statBonus = "WIL",
                type = ExerciseType.TIMER,
                sets = listOf(15 * m, 20 * m, 15 * m), // 3 Series de tiempo
                restSeconds = 60
            )
        )
    }
}