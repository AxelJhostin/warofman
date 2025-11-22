package com.negocio.warofmen.navigation

// Sealed class: Define un conjunto cerrado de opciones. Solo existen estas pantallas.
sealed class AppScreens(val route: String) {
    object Creation : AppScreens("creation_screen")
    object Home : AppScreens("home_screen")
    object Stats : AppScreens("stats_screen")
}