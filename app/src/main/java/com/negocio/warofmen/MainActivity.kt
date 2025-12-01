package com.negocio.warofmen

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge // <--- IMPORTANTE: Habilita el modo pantalla completa
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
import com.negocio.warofmen.core.navigation.AppScreens
import com.negocio.warofmen.core.util.GameViewModelFactory
import com.negocio.warofmen.ui.theme.WarOfMenTheme
import com.negocio.warofmen.ui.viewmodel.CreationViewModel
import com.negocio.warofmen.ui.viewmodel.HomeViewModel
import com.negocio.warofmen.ui.viewmodel.SettingsViewModel

// Pantallas
import com.negocio.warofmen.ui.screens.creation.CreationScreen
import com.negocio.warofmen.ui.screens.PantallaEntrenamiento
import com.negocio.warofmen.ui.screens.PantallaEstadisticas
import com.negocio.warofmen.ui.screens.PantallaGraficos
import com.negocio.warofmen.ui.screens.PantallaJuego
import com.negocio.warofmen.ui.screens.SettingsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. ACTIVAR MODO EDGE-TO-EDGE
        // Esto permite que la app dibuje detrás de las barras de sistema
        enableEdgeToEdge()

        setContent {
            WarOfMenTheme {
                // 2. SCAFFOLD GLOBAL
                // Este componente calcula automáticamente el espacio necesario para las barras de sistema
                // y nos entrega 'innerPadding' para que nuestro contenido no se solape.
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->

                    // Aplicamos el padding calculado a todo el contenedor de navegación
                    Box(modifier = Modifier.padding(innerPadding)) {

                        // 3. CONFIGURACIÓN INICIAL
                        val navController = rememberNavController()
                        val context = LocalContext.current
                        val app = context.applicationContext as Application
                        val factory = GameViewModelFactory(app)

                        // 4. VIEWMODEL PRINCIPAL
                        val homeViewModel: HomeViewModel = viewModel(factory = factory)
                        val gameState by homeViewModel.gameState.collectAsState()

                        // 5. SISTEMA DE NAVEGACIÓN
                        NavHost(
                            navController = navController,
                            startDestination = AppScreens.Home.route
                        ) {

                            // --- RUTA: HOME (PANTALLA PRINCIPAL) ---
                            composable(AppScreens.Home.route) {
                                val isLoaded by homeViewModel.isDataLoaded.collectAsState()

                                if (!isLoaded) {
                                    // MODO CARGA
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    }
                                } else {
                                    // DATOS LISTOS
                                    LaunchedEffect(gameState.isCreated) {
                                        if (!gameState.isCreated) {
                                            navController.navigate(AppScreens.Creation.route) {
                                                popUpTo(0)
                                            }
                                        }
                                    }

                                    if (gameState.isCreated) {
                                        // NOTA: Usamos Box en lugar de Scaffold interno para mayor control
                                        Box(modifier = Modifier.fillMaxSize()) {
                                            PantallaJuego(
                                                viewModel = homeViewModel,
                                                onStartQuest = {
                                                    navController.navigate(AppScreens.Workout.route)
                                                },
                                                onOpenSettings = {
                                                    navController.navigate(AppScreens.Settings.route)
                                                }
                                            )

                                            // Botones Flotantes (Stats y Charts) posicionados manualmente
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
                        }
                    }
                }
            }
        }
    }
}