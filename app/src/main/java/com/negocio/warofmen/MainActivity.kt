package com.negocio.warofmen

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.negocio.warofmen.navigation.AppScreens
import com.negocio.warofmen.ui.theme.WarOfMenTheme
import com.negocio.warofmen.util.GameViewModelFactory
import com.negocio.warofmen.viewmodel.CreationViewModel
import com.negocio.warofmen.viewmodel.HomeViewModel
import com.negocio.warofmen.vista.PantallaCreacion
import com.negocio.warofmen.vista.PantallaEntrenamiento
import com.negocio.warofmen.vista.PantallaEstadisticas
import com.negocio.warofmen.vista.PantallaGraficos
import com.negocio.warofmen.vista.PantallaJuego

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WarOfMenTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    val navController = rememberNavController()
                    val context = LocalContext.current
                    val app = context.applicationContext as Application
                    val factory = GameViewModelFactory(app)

                    val homeViewModel: HomeViewModel = viewModel(factory = factory)
                    val gameState by homeViewModel.gameState.collectAsState()

                    NavHost(
                        navController = navController,
                        startDestination = AppScreens.Home.route
                    ) {

                        // --- HOME ---
                        composable(AppScreens.Home.route) {
                            LaunchedEffect(gameState.isCreated) {
                                if (!gameState.isCreated) {
                                    navController.navigate(AppScreens.Creation.route) { popUpTo(0) }
                                }
                            }

                            if (gameState.isCreated) {
                                Scaffold(
                                    floatingActionButton = {
                                        // Un solo botón limpio para ir a ver las stats
                                        FloatingActionButton(
                                            onClick = { navController.navigate(AppScreens.Stats.route) },
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ) {
                                            Text("📊")
                                        }
                                    }
                                ) { padding ->
                                    Box(modifier = Modifier.padding(padding)) {
                                        PantallaJuego(
                                            viewModel = homeViewModel,
                                            onStartQuest = { navController.navigate(AppScreens.Workout.route) }
                                        )
                                    }
                                }
                            }
                        }

                        // --- STATS (Con botón a Charts) ---
                        composable(AppScreens.Stats.route) {
                            PantallaEstadisticas(
                                player = gameState,
                                onBack = { navController.popBackStack() },
                                onUpdateWeight = { w -> homeViewModel.updateWeight(w) },
                                onNavigateToCharts = { navController.navigate(AppScreens.Charts.route) }
                            )
                        }

                        // --- CHARTS (Gráficas de ejercicio) ---
                        composable(AppScreens.Charts.route) {
                            PantallaGraficos(
                                workoutLogs = gameState.workoutLogs,
                                level = gameState.level,
                                onBack = { navController.popBackStack() }
                            )
                        }

                        // --- OTRAS PANTALLAS ---
                        composable(AppScreens.Creation.route) {
                            val creationViewModel: CreationViewModel = viewModel(factory = factory)
                            PantallaCreacion(
                                viewModel = creationViewModel,
                                onFinished = {
                                    navController.navigate(AppScreens.Home.route) {
                                        popUpTo(AppScreens.Creation.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(AppScreens.Workout.route) {
                            PantallaEntrenamiento(
                                viewModel = homeViewModel,
                                onNavigateBack = {
                                    homeViewModel.clearActiveQuest()
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}