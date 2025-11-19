package com.negocio.warofmen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.negocio.warofmen.ui.theme.WarOfMenTheme
import com.negocio.warofmen.viewmodel.GameViewModel
import com.negocio.warofmen.vista.PantallaCreacion
import com.negocio.warofmen.vista.PantallaEstadisticas
import com.negocio.warofmen.vista.PantallaJuego

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializamos el ViewModel
        val viewModel: GameViewModel by viewModels()

        setContent {
            // Aplicamos tu tema personalizado
            WarOfMenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Observamos los estados principales
                    val state by viewModel.gameState.collectAsState()
                    val isViewingStats by viewModel.isViewingStats.collectAsState()

                    // Lógica de Navegación
                    if (!state.isCreated) {
                        // 1. Si el usuario no ha sido creado -> Pantalla de Registro
                        PantallaCreacion(viewModel)

                    } else if (isViewingStats) {
                        // 2. Si dio click al botón de stats -> Pantalla Estadísticas
                        PantallaEstadisticas(
                            player = state,
                            onBack = { viewModel.toggleStatsView() },
                            onUpdateWeight = { newWeight ->
                                // Conectamos la UI con la lógica del ViewModel
                                viewModel.updateWeight(newWeight)
                            }
                        )

                    } else {
                        // 3. Pantalla Principal de Juego (Misiones)
                        Scaffold(
                            floatingActionButton = {
                                FloatingActionButton(
                                    onClick = { viewModel.toggleStatsView() },
                                    containerColor = MaterialTheme.colorScheme.primary
                                ) {
                                    Text("📊") // Icono de estadísticas
                                }
                            }
                        ) { paddingValues ->
                            Box(modifier = Modifier.padding(paddingValues)) {
                                PantallaJuego(viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}