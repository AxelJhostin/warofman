package com.axeljhostin.warofmen

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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

// --- IMPORTS ---
import com.axeljhostin.warofmen.core.navigation.AppScreens
import com.axeljhostin.warofmen.core.util.GameViewModelFactory
import com.axeljhostin.warofmen.ui.theme.WarOfMenTheme
import com.axeljhostin.warofmen.ui.viewmodel.CreationViewModel
import com.axeljhostin.warofmen.ui.viewmodel.HomeViewModel
import com.axeljhostin.warofmen.ui.viewmodel.SettingsViewModel

// Pantallas
import com.axeljhostin.warofmen.ui.screens.creation.CreationScreen
import com.axeljhostin.warofmen.ui.screens.PantallaEntrenamiento
import com.axeljhostin.warofmen.ui.screens.PantallaEstadisticas
import com.axeljhostin.warofmen.ui.screens.PantallaGraficos
import com.axeljhostin.warofmen.ui.screens.PantallaJuego
import com.axeljhostin.warofmen.ui.screens.SettingsScreen
import com.axeljhostin.warofmen.ui.screens.PantallaRacha // Nueva Pantalla

//Sonidos
import com.axeljhostin.warofmen.core.util.SoundManager
import com.axeljhostin.warofmen.ui.screens.PantallaDetalleReto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundManager.init(applicationContext)
        enableEdgeToEdge()

        setContent {
            WarOfMenTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->

                    Box(modifier = Modifier.padding(innerPadding)) {

                        // Configuración Inicial
                        val navController = rememberNavController()
                        val context = LocalContext.current
                        val app = context.applicationContext as Application
                        val factory = GameViewModelFactory(app)

                        // ViewModel Principal (Compartido)
                        val homeViewModel: HomeViewModel = viewModel(factory = factory)
                        val gameState by homeViewModel.gameState.collectAsState()

                        // SISTEMA DE NAVEGACIÓN
                        NavHost(
                            navController = navController,
                            startDestination = AppScreens.Home.route
                        ) {

                            // --- RUTA: HOME ---
                            composable(AppScreens.Home.route) {
                                val isLoaded by homeViewModel.isDataLoaded.collectAsState()

                                if (!isLoaded) {
                                    // Carga
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    }
                                } else {
                                    // Datos listos
                                    LaunchedEffect(gameState.isCreated) {
                                        if (!gameState.isCreated) {
                                            navController.navigate(AppScreens.Creation.route) {
                                                popUpTo(0)
                                            }
                                        }
                                    }

                                    if (gameState.isCreated) {
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            PantallaJuego(
                                                viewModel = homeViewModel,
                                                onStartQuest = {
                                                    navController.navigate(AppScreens.Workout.route)
                                                },
                                                onOpenSettings = {
                                                    navController.navigate(AppScreens.Settings.route)
                                                },
                                                // CONEXIÓN A RACHAS (NUEVO)
                                                onOpenStreak = {
                                                    navController.navigate(AppScreens.Streak.route)
                                                },
                                                onOpenChallengeDetail = {
                                                    navController.navigate("challenge_detail")
                                                }
                                            )

                                            // Botones Flotantes
                                            Column(
                                                modifier = Modifier
                                                    .align(Alignment.BottomEnd)
                                                    .padding(16.dp),
                                                horizontalAlignment = Alignment.End
                                            ) {
                                                FloatingActionButton(
                                                    onClick = { navController.navigate(AppScreens.Charts.route) },
                                                    containerColor = MaterialTheme.colorScheme.secondary,
                                                    modifier = Modifier.padding(bottom = 16.dp)
                                                ) {
                                                    Text("📈")
                                                }

                                                FloatingActionButton(
                                                    onClick = { navController.navigate(AppScreens.Stats.route) },
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ) {
                                                    Text("📊")
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // --- RUTA: CREACIÓN ---
                            composable(AppScreens.Creation.route) {
                                val creationViewModel: CreationViewModel = viewModel(factory = factory)
                                CreationScreen(
                                    viewModel = creationViewModel,
                                    onFinished = {
                                        navController.navigate(AppScreens.Home.route) {
                                            popUpTo(AppScreens.Creation.route) { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // --- RUTA: ESTADÍSTICAS ---
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

                            // --- RUTA: ENTRENAMIENTO ---
                            composable(AppScreens.Workout.route) {
                                PantallaEntrenamiento(
                                    viewModel = homeViewModel,
                                    onNavigateBack = {
                                        homeViewModel.clearActiveQuest()
                                        navController.popBackStack()
                                    }
                                )
                            }

                            // --- RUTA: GRÁFICOS ---
                            composable(AppScreens.Charts.route) {
                                PantallaGraficos(
                                    workoutLogs = gameState.workoutLogs,
                                    level = gameState.level,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // --- RUTA: AJUSTES ---
                            composable(AppScreens.Settings.route) {
                                val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
                                SettingsScreen(
                                    viewModel = settingsViewModel,
                                    onBack = { navController.popBackStack() },
                                    onResetComplete = {
                                        navController.navigate(AppScreens.Home.route) {
                                            popUpTo(0)
                                        }
                                    }
                                )
                            }

                            // --- RUTA: RACHA / CAMINO DE DISCIPLINA (NUEVO) ---
                            composable(AppScreens.Streak.route) {
                                PantallaRacha(
                                    currentStreak = gameState.currentStreak,
                                    onBack = { navController.popBackStack() }
                                )
                            }

                            // --- RUTA: DETALLE RETO ---
                            composable("challenge_detail") { // O usa AppScreens.ChallengeDetail.route
                                PantallaDetalleReto(
                                    viewModel = homeViewModel,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Liberar memoria de audio al cerrar
        SoundManager.release()
    }
}