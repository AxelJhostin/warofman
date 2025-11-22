package com.negocio.warofmen

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.negocio.warofmen.vista.PantallaEstadisticas
import com.negocio.warofmen.vista.PantallaJuego

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

                    // 2. Instanciamos el ViewModel Principal aquí para compartirlo entre Home y Stats
                    val homeViewModel: HomeViewModel = viewModel(factory = factory)
                    val gameState by homeViewModel.gameState.collectAsState()

                    // 3. SISTEMA DE NAVEGACIÓN
                    // Iniciamos siempre en HOME. Si no hay usuario, Home nos redirige a Creation.
                    NavHost(
                        navController = navController,
                        startDestination = AppScreens.Home.route
                    ) {

                        // --- RUTA: PANTALLA DE JUEGO (HOME) ---
                        composable(AppScreens.Home.route) {
                            // Verificación de seguridad: Si no hay personaje, ir a Creación
                            LaunchedEffect(gameState.isCreated) {
                                if (!gameState.isCreated) {
                                    navController.navigate(AppScreens.Creation.route) {
                                        popUpTo(0) // Borra el historial para no volver atrás
                                    }
                                }
                            }

                            // Si existe personaje, mostramos el juego
                            if (gameState.isCreated) {
                                // Usamos un Scaffold simple aquí o dentro de la pantalla
                                // Para mantener la lógica de navegación limpia, pasamos la acción de ir a stats
                                androidx.compose.material3.Scaffold(
                                    floatingActionButton = {
                                        androidx.compose.material3.FloatingActionButton(
                                            onClick = { navController.navigate(AppScreens.Stats.route) },
                                            containerColor = MaterialTheme.colorScheme.primary
                                        ) {
                                            androidx.compose.material3.Text("📊")
                                        }
                                    }
                                ) { padding ->
                                    androidx.compose.foundation.layout.Box(
                                        modifier = androidx.compose.ui.Modifier.padding(padding)
                                    ) {
                                        PantallaJuego(viewModel = homeViewModel)
                                    }
                                }
                            }
                        }

                        // --- RUTA: PANTALLA DE CREACIÓN ---
                        composable(AppScreens.Creation.route) {
                            // Instanciamos el ViewModel de creación solo cuando se necesita
                            val creationViewModel: CreationViewModel = viewModel(factory = factory)

                            PantallaCreacion(
                                viewModel = creationViewModel,
                                onFinished = {
                                    // Al terminar, vamos al Home y borramos la pantalla de creación del historial
                                    navController.navigate(AppScreens.Home.route) {
                                        popUpTo(AppScreens.Creation.route) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- RUTA: PANTALLA DE ESTADÍSTICAS ---
                        composable(AppScreens.Stats.route) {
                            PantallaEstadisticas(
                                player = gameState, // Usamos el estado del homeViewModel compartido
                                onBack = { navController.popBackStack() },
                                onUpdateWeight = { newWeight ->
                                    homeViewModel.updateWeight(newWeight)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}