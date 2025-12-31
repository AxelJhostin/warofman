package com.negocio.warofmen.core.navigation

// Sealed class: Define un conjunto cerrado de opciones. Solo existen estas pantallas.
sealed class AppScreens(val route: String) {
    object Creation : AppScreens("creation_screen")
    object Home : AppScreens("home_screen")
    object Stats : AppScreens("stats_screen")
    object Workout : AppScreens("workout_screen")
    object Charts : AppScreens("charts_screen")
    object Settings : AppScreens("settings_screen")
    object Streak : AppScreens("streak_screen")
    object ChallengeDetail : AppScreens("challenge_detail")
}