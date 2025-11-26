package com.negocio.warofmen

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// --- IMPORTS ACTUALIZADOS SEGÚN TU NUEVA ESTRUCTURA ---
import com.negocio.warofmen.core.navigation.AppScreens
import com.negocio.warofmen.core.util.GameViewModelFactory
import com.negocio.warofmen.ui.theme.WarOfMenTheme
import com.negocio.warofmen.ui.viewmodel.CreationViewModel
import com.negocio.warofmen.ui.viewmodel.HomeViewModel

// Pantallas
import com.negocio.warofmen.ui.screens.creation.CreationScreen // El nuevo Wizard
import com.negocio.warofmen.ui.screens.PantallaEntrenamiento
import com.negocio.warofmen.ui.screens.PantallaEstadisticas
import com.negocio.warofmen.ui.screens.PantallaGraficos
import com.negocio.warofmen.ui.screens.PantallaJuego

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WarOfMenTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    // 1. Configuración Inicial
                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val app = context.applicationContext as Application
                    val factory = GameViewModelFactory(app)

                    // 2. Instanciamos el ViewModel Principal
                    val homeViewModel: HomeViewModel = viewModel(factory = factory)
                    val gameState by homeViewModel.gameState.collectAsState()

                    // 3. SISTEMA DE NAVEGACIÓN
                    NavHost(
                        navController = navController,
                        startDestination = AppScreens.Home.route
                    ) {

                        // --- RUTA: HOME (PANTALLA PRINCIPAL) ---
                        composable(AppScreens.Home.route) {
                            // Seguridad: Si no hay personaje, ir a Creación
                            LaunchedEffect(gameState.isCreated) {
                                if (!gameState.isCreated) {
                                    navController.navigate(AppScreens.Creation.route) {
                                        popUpTo(0) // Borra el historial para no volver atrás
                                    }
                                }
                            }

                            // Si existe personaje, mostramos el juego
                            if (gameState.isCreated) {
                                Scaffold(
                                    floatingActionButton = {
                                        // Columna para tener 2 botones flotantes (Stats y Charts)
                                        Column(
                                            horizontalAlignment = Alignment.End
                                        ) {
                                            // Botón 1: Gráficos de Ejercicio
                                            FloatingActionButton(
                                                onClick = { navController.navigate(AppScreens.Charts.route) },
                                                containerColor = MaterialTheme.colorScheme.secondary,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                            ) {
                                                Text("📈")
                                            }

                                            // Botón 2: Estadísticas del Personaje
                                            FloatingActionButton(
                                                onClick = { navController.navigate(AppScreens.Stats.route) },
                                                containerColor = MaterialTheme.colorScheme.primary
                                            ) {
                                                Text("📊")
                                            }
                                        }
                                    }
                                ) { padding ->
                                    Box(modifier = Modifier.padding(padding)) {
                                        PantallaJuego(
                                            viewModel = homeViewModel,
                                            onStartQuest = {
                                                // Ir a la pantalla naranja de entrenamiento
                                                navController.navigate(AppScreens.Workout.route)
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // --- RUTA: CREACIÓN (EL NUEVO WIZARD) ---
                        composable(AppScreens.Creation.route) {
                            val creationViewModel: CreationViewModel = viewModel(factory = factory)

                            // Usamos la nueva CreationScreen
                            CreationScreen(
                                viewModel = creationViewModel,
                                onFinished = {
                                    // Al terminar, vamos al Home y borramos la creación del historial
                                    navController.navigate(AppScreens.Home.route) {
                                        popUpTo(AppScreens.Creation.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- RUTA: ESTADÍSTICAS DEL PERSONAJE ---
                        composable(AppScreens.Stats.route) {
                            PantallaEstadisticas(
                                player = gameState,
                                onBack = { navController.popBackStack() },
                                onUpdateWeight = { newWeight ->
                                    homeViewModel.updateWeight(newWeight)
                                },
                                onNavigateToCharts = {
                                    navController.navigate(AppScreens.Charts.route)
                                }
                            )
                        }

                        // --- RUTA: ENTRENAMIENTO (PANTALLA NARANJA) ---
                        composable(AppScreens.Workout.route) {
                            PantallaEntrenamiento(
                                viewModel = homeViewModel,
                                onNavigateBack = {
                                    homeViewModel.clearActiveQuest()
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- RUTA: GRÁFICOS DE RENDIMIENTO ---
                        composable(AppScreens.Charts.route) {
                            PantallaGraficos(
                                workoutLogs = gameState.workoutLogs,
                                level = gameState.level,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}