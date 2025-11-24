package com.negocio.warofmen.vista

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.dato.PlayerCharacter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// --- COLORES Y ESTILOS LOCALES PARA ESTA PANTALLA ---
val DarkBackgroundLocal = Color(0xFF121212)
val PanelBackgroundLocal = Color(0xFF1E1E1E)
val GraphLineColorLocal = Color(0xFF00E5FF) // Cian Neon
val StatStrColorLocal = Color(0xFFF44336)   // Rojo
val StatAgiColorLocal = Color(0xFF4CAF50)   // Verde
val StatStaColorLocal = Color(0xFFFFC107)   // Amarillo
val StatWilColorLocal = Color(0xFF2196F3)   // Azul
val StatLukColorLocal = Color(0xFF9C27B0)   // Morado

@Composable
fun PantallaEstadisticas(
    player: PlayerCharacter,
    onBack: () -> Unit,
    onUpdateWeight: (Float) -> Unit,
    onNavigateToCharts: () -> Unit
) {
    // Estado para mostrar/ocultar el diálogo de peso
    var showWeightDialog by remember { mutableStateOf(false) }

    // Lógica del Popup de Peso
    if (showWeightDialog) {
        WeightUpdateDialog(
            onDismiss = { showWeightDialog = false },
            onConfirm = { newWeight ->
                onUpdateWeight(newWeight)
                showWeightDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackgroundLocal) // Fondo oscuro
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 1. HEADER (Botón volver y Título)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) { Text("🔙") }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "ESTADÍSTICAS",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. TARJETA DE PERSONAJE (STATS RPG)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                .background(PanelBackgroundLocal, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                // Nombre y Nivel
                Text(
                    text = player.name.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
                Text(
                    text = "Nivel ${player.level} ${player.gender}",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )

                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))

                // Barras de Atributos
                RpgStatBar("FUERZA (STR)", player.strength, StatStrColorLocal)
                RpgStatBar("AGILIDAD (AGI)", player.agility, StatAgiColorLocal)
                RpgStatBar("RESISTENCIA (STA)", player.stamina, StatStaColorLocal)
                RpgStatBar("VOLUNTAD (WIL)", player.willpower, StatWilColorLocal)
                RpgStatBar("SUERTE (LUK)", player.luck, StatLukColorLocal)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. SECCIÓN DE GRÁFICA DE PESO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "HISTORIAL DE PESO",
                color = Color.Gray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            // Botón flotante pequeño para añadir peso
            SmallFloatingActionButton(
                onClick = { showWeightDialog = true },
                containerColor = GraphLineColorLocal,
                contentColor = Color.Black
            ) {
                Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Componente Gráfica de Peso
        WeightChart(history = player.weightHistory)

        Spacer(modifier = Modifier.height(16.dp))

        // 4. DATOS BIOMÉTRICOS (Resumen numérico)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            BioMetricCard("PESO ACTUAL", "${player.weight} kg")
            BioMetricCard("IMC", "%.1f".format(player.bmi))

            // Calculamos el cambio desde el inicio
            val firstWeight = getWeightFromHistory(player.weightHistory.firstOrNull())
            val diff = player.weight - firstWeight
            val sign = if (diff > 0) "+" else "" // Si subió pone +, si bajó pone - automático
            BioMetricCard("CAMBIO", "$sign%.1f kg".format(diff))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 5. BOTÓN GRANDE PARA IR A GRÁFICAS DE EJERCICIO
        Button(
            onClick = onNavigateToCharts,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PanelBackgroundLocal),
            border = androidx.compose.foundation.BorderStroke(1.dp, GraphLineColorLocal),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "📈  VER RENDIMIENTO DE EJERCICIOS",
                color = GraphLineColorLocal,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp)) // Espacio final para scroll
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun RpgStatBar(label: String, value: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "$value",
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Barra Visual
        val progress = (value % 50) / 50f
        val visualProgress = if (value > 0 && progress == 0f) 1f else progress

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CutCornerShape(bottomEnd = 8.dp))
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if (value > 0) visualProgress + 0.05f else 0f)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(color.copy(alpha = 0.5f), color)
                        )
                    )
            )
        }
    }
}

@Composable
fun WeightChart(history: List<String>) {
    // Convertimos la lista de Strings "Tiempo:Peso" a objetos usables
    val dataPoints = history.map { entry ->
        val parts = entry.split(":")
        val time = parts[0].toLongOrNull() ?: 0L
        val weight = parts[1].toFloatOrNull() ?: 0f
        Pair(time, weight)
    }.sortedBy { it.first }

    if (dataPoints.isEmpty()) {
        Text(
            "No hay datos registrados aún.",
            color = Color.Gray,
            modifier = Modifier.padding(8.dp)
        )
        return
    }

    val weights = dataPoints.map { it.second }
    val minWeight = (weights.minOrNull() ?: 0f) - 2f
    val maxWeight = (weights.maxOrNull() ?: 100f) + 2f

    Card(
        colors = CardDefaults.cardColors(containerColor = PanelBackgroundLocal),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp)
            ) {
                val width = size.width
                val height = size.height

                // Caso especial: Solo 1 punto
                if (dataPoints.size == 1) {
                    drawLine(
                        color = GraphLineColorLocal,
                        start = Offset(0f, height / 2),
                        end = Offset(width, height / 2),
                        strokeWidth = 5f
                    )
                    return@Canvas
                }

                val path = Path()
                val xStep = width / (dataPoints.size - 1)

                dataPoints.forEachIndexed { index, point ->
                    val x = index * xStep
                    val normalizedY = (point.second - minWeight) / (maxWeight - minWeight)
                    val y = height - (normalizedY * height)

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    // Dibujar punto
                    drawCircle(color = GraphLineColorLocal, radius = 6f, center = Offset(x, y))
                }

                // Dibujar línea conectora
                drawPath(
                    path = path,
                    color = GraphLineColorLocal,
                    style = Stroke(width = 4f)
                )

                // Relleno degradado bajo la línea
                val fillPath = Path()
                fillPath.addPath(path)
                fillPath.lineTo(width, height)
                fillPath.lineTo(0f, height)
                fillPath.close()

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GraphLineColorLocal.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
            }

            // Fechas Inicio y Fin
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    formatDate(dataPoints.first().first),
                    color = Color.Gray,
                    fontSize = 10.sp
                )
                Text(
                    formatDate(dataPoints.last().first),
                    color = Color.Gray,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun BioMetricCard(title: String, value: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
        modifier = Modifier.width(105.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                title,
                color = Color.Gray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                value,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun WeightUpdateDialog(onDismiss: () -> Unit, onConfirm: (Float) -> Unit) {
    var weightInput by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Actualizar Peso") },
        text = {
            OutlinedTextField(
                value = weightInput,
                onValueChange = {
                    if (it.all { char -> char.isDigit() || char == '.' }) weightInput = it
                },
                label = { Text("Peso en kg") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                val w = weightInput.toFloatOrNull()
                if (w != null && w > 0) onConfirm(w)
            }) { Text("GUARDAR") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// --- UTILS LOCALES ---
fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun getWeightFromHistory(entry: String?): Float {
    if (entry == null) return 0f
    return entry.split(":")[1].toFloatOrNull() ?: 0f
}