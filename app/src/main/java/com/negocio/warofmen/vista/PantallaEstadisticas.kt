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

// --- COLORES Y ESTILOS RPG ---
val DarkBackground = Color(0xFF121212)
val PanelBackground = Color(0xFF1E1E1E)
val GraphLineColor = Color(0xFF00E5FF) // Cian Neon para la gráfica
val StatStrColor = Color(0xFFF44336)   // Rojo
val StatAgiColor = Color(0xFF4CAF50)   // Verde
val StatStaColor = Color(0xFFFFC107)   // Amarillo
val StatWilColor = Color(0xFF2196F3)   // Azul
val StatLukColor = Color(0xFF9C27B0)   // Morado

@Composable
fun PantallaEstadisticas(
    player: PlayerCharacter,
    onBack: () -> Unit,
    onUpdateWeight: (Float) -> Unit
) {
    // Estado para mostrar/ocultar el diálogo de peso
    var showWeightDialog by remember { mutableStateOf(false) }

    // Lógica del Popup
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
            .background(DarkBackground) // Fondo oscuro "Gamer"
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
                .background(PanelBackground, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                // Nombre y Nivel
                Text(player.name.uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                Text("Nivel ${player.level} ${player.gender}", color = Color.LightGray, fontSize = 14.sp)

                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))

                // Barras de Atributos
                RpgStatBar("FUERZA (STR)", player.strength, StatStrColor)
                RpgStatBar("AGILIDAD (AGI)", player.agility, StatAgiColor)
                RpgStatBar("RESISTENCIA (STA)", player.stamina, StatStaColor)
                RpgStatBar("VOLUNTAD (WIL)", player.willpower, StatWilColor)
                RpgStatBar("SUERTE (LUK)", player.luck, StatLukColor)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. SECCIÓN DE GRÁFICA DE PESO
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("HISTORIAL DE PESO", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold)

            // Botón flotante pequeño para añadir peso
            SmallFloatingActionButton(
                onClick = { showWeightDialog = true },
                containerColor = GraphLineColor,
                contentColor = Color.Black
            ) {
                Text("+", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Componente Gráfica
        WeightChart(history = player.weightHistory)

        Spacer(modifier = Modifier.height(16.dp))

        // 4. DATOS BIOMÉTRICOS (Resumen numérico)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            BioMetricCard("PESO ACTUAL", "${player.weight} kg")
            BioMetricCard("IMC", "%.1f".format(player.bmi))

            // Calculamos el cambio desde el inicio
            val firstWeight = getWeightFromHistory(player.weightHistory.firstOrNull())
            val diff = player.weight - firstWeight
            val sign = if(diff > 0) "+" else "" // Si subió pone +, si bajó pone - automático
            BioMetricCard("CAMBIO", "$sign%.1f kg".format(diff))
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
            Text(label, color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "$value",
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Barra Visual: Usamos Módulo 50 para que la barra se llene y reinicie visualmente al subir mucho
        // pero el número sigue subiendo eternamente.
        val progress = (value % 50) / 50f
        val visualProgress = if (value > 0 && progress == 0f) 1f else progress // Si es 50, 100, etc se ve llena

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CutCornerShape(bottomEnd = 8.dp)) // Corte futurista en la esquina
                .background(Color.Black)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(if(value > 0) visualProgress + 0.05f else 0f) // Mínimo visual
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
        Text("No hay datos registrados aún.", color = Color.Gray, modifier = Modifier.padding(8.dp))
        return
    }

    val weights = dataPoints.map { it.second }
    val minWeight = (weights.minOrNull() ?: 0f) - 2f
    val maxWeight = (weights.maxOrNull() ?: 100f) + 2f

    Card(
        colors = CardDefaults.cardColors(containerColor = PanelBackground),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Canvas(modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Ocupa todo el espacio disponible en la tarjeta
                .padding(8.dp)
            ) {
                val width = size.width
                val height = size.height

                // Caso especial: Solo 1 punto
                if (dataPoints.size == 1) {
                    drawLine(
                        color = GraphLineColor,
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
                    // Normalizar Y para que entre en la gráfica
                    val normalizedY = (point.second - minWeight) / (maxWeight - minWeight)
                    val y = height - (normalizedY * height)

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }
                    // Dibujar punto
                    drawCircle(color = GraphLineColor, radius = 6f, center = Offset(x, y))
                }

                // Dibujar línea conectora
                drawPath(
                    path = path,
                    color = GraphLineColor,
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
                        colors = listOf(GraphLineColor.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
            }

            // Fechas Inicio y Fin
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(formatDate(dataPoints.first().first), color = Color.Gray, fontSize = 10.sp)
                Text(formatDate(dataPoints.last().first), color = Color.Gray, fontSize = 10.sp)
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
            Text(title, color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) weightInput = it },
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

// --- UTILS ---
fun formatDate(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
    return formatter.format(Date(millis))
}

fun getWeightFromHistory(entry: String?): Float {
    if (entry == null) return 0f
    return entry.split(":")[1].toFloatOrNull() ?: 0f
}