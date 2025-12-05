package com.negocio.warofmen.data.source

import com.negocio.warofmen.data.model.StreakMilestone

object MilestoneProvider {

    val milestones = listOf(
        StreakMilestone(3, "El Despertar", "Has iniciado el camino. La disciplina comienza a formarse.", 1.0f),
        StreakMilestone(7, "Hábito Formado", "Una semana de constancia. Ganas +10% de XP en todo.", 1.1f),
        StreakMilestone(14, "Guerrero Constante", "Dos semanas sin fallar. Ganas +15% de XP.", 1.15f),
        StreakMilestone(30, "Llama Eterna", "¡FUEGO AZUL DESBLOQUEADO! Tu voluntad es inquebrantable. +20% XP.", 1.2f, isBlueFire = true),
        StreakMilestone(60, "Maestro de la Disciplina", "La consistencia es tu arma. +30% XP.", 1.3f, isBlueFire = true),
        StreakMilestone(100, "Leyenda Viviente", "Un centenar de batallas ganadas. +50% XP.", 1.5f, isBlueFire = true)
    )

    // Función para obtener el multiplicador actual basado en tu racha
    fun getCurrentMultiplier(streak: Int): Float {
        // Buscamos el hito más alto que hayas superado
        val milestone = milestones.filter { it.daysRequired <= streak }.maxByOrNull { it.daysRequired }
        return milestone?.xpMultiplier ?: 1.0f
    }

    // Función para saber si tienes fuego azul
    fun hasBlueFire(streak: Int): Boolean {
        return streak >= 30 // A los 30 días se pone azul
    }
}