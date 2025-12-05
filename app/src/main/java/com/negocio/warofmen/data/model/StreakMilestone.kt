package com.negocio.warofmen.data.model

data class StreakMilestone(
    val daysRequired: Int,
    val title: String,
    val description: String,
    val xpMultiplier: Float, // Ej: 1.1 para 10% extra
    val isBlueFire: Boolean = false // ¿Desbloquea el fuego azul?
)