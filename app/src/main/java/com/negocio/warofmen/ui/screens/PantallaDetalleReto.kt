package com.negocio.warofmen.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.ui.theme.*
import com.negocio.warofmen.ui.viewmodel.HomeViewModel
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun PantallaDetalleReto(
    viewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val challenge = gameState.activeChallenge ?: return

    // --- 1. MOTOR MATEMÁTICO ---

    val startWeight = challenge.startWeight
    val targetWeight = challenge.targetWeight
    val currentWeight = gameState.currentWeight
    val totalDeltaWeight = startWeight - targetWeight
    val isWeightLoss = totalDeltaWeight > 0

    val startDate = challenge.startDate
    val deadline = challenge.deadline
    val now = System.currentTimeMillis()
    val totalDurationMillis = deadline - startDate
    val elapsedMillis = now - startDate

    // Tiempos
    val totalDays = (totalDurationMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(1)
    val daysPassed = (elapsedMillis / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
    val daysRemaining = (totalDays - daysPassed).coerceAtLeast(0)

    // Progreso
    val progress = GameUtils.calculateChallengeProgress(startWeight, currentWeight, targetWeight)

    // Ritmos (Kg/Semana)
    val weeksTotal = totalDays / 7f
    val weeksPassedFloat = daysPassed / 7f

    // ¿Cuánto hay que bajar CADA SEMANA para llegar a tiempo?
    val requiredRate = if (weeksTotal > 0) abs(totalDeltaWeight) / weeksTotal else 0f

    // ¿Cuánto estás bajando REALMENTE?
    val currentDelta = abs(startWeight - currentWeight)
    val actualRate = if (weeksPassedFloat > 0.1f) currentDelta / weeksPassedFloat else 0f

    // Proyección
    val timeProgress = elapsedMillis.toFloat() / totalDurationMillis.toFloat()
    val safeTimeProgress = timeProgress.coerceIn(0f, 1f)
    val expectedWeightToday = startWeight - (totalDeltaWeight * safeTimeProgress)

    // Estado
    val diff = if (isWeightLoss) currentWeight - expectedWeightToday else expectedWeightToday - currentWeight
    val status = when {
        diff > 0.5f -> ChallengeStatus.BEHIND
        diff < -0.5f -> ChallengeStatus.AHEAD
        else -> ChallengeStatus.ON_TRACK
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RpgBackground)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.Gray)
            }
            Text("ESTRATEGIA DETALLADA", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- CÍRCULO DASHBOARD ---
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.3f)
                .padding(8.dp)
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 12.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                // Fondo
                drawCircle(color = Color.DarkGray.copy(alpha = 0.3f), radius = radius, style = Stroke(width = strokeWidth))

                // Progreso
                drawArc(
                    color = status.color,
                    startAngle = -90f,
                    sweepAngle = 360 * progress,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // 10 Nodos (Decágono de progreso)
                val totalNodes = 10
                for (i in 0 until totalNodes) {
                    val angle = Math.toRadians((i * 360 / totalNodes).toDouble() - 90)
                    val x = center.x + radius * cos(angle).toFloat()
                    val y = center.y + radius * sin(angle).toFloat()
                    val nodeProgress = i.toFloat() / totalNodes
                    val isLit = progress >= nodeProgress

                    drawCircle(color = RpgBackground, radius = 5.dp.toPx(), center = Offset(x, y))
                    drawCircle(color = if (isLit) status.color else Color.Gray, radius = 4.dp.toPx(), center = Offset(x, y))
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("PESO ACTUAL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("$currentWeight", color = Color.White, fontSize = 56.sp, fontWeight = FontWeight.ExtraBold)
                Text("KG", color = status.color, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(8.dp))

                Surface(color = status.color.copy(alpha = 0.15f), shape = RoundedCornerShape(50), border = androidx.compose.foundation.BorderStroke(1.dp, status.color)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
                        Icon(status.icon, contentDescription = null, tint = status.color, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(status.label, color = status.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // --- CRONOGRAMA ---
        Row(Modifier.fillMaxWidth()) {
            InfoBox(label = "INICIO", value = GameUtils.formatDate(startDate), icon = Icons.Default.DateRange, color = Color.Gray, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            InfoBox(label = "RESTANTE", value = "$daysRemaining DÍAS", icon = Icons.Default.Warning, color = if (daysRemaining < 7) Color.Red else RpgNeonCyan, modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(8.dp))
            InfoBox(label = "META", value = GameUtils.formatDate(deadline), icon = Icons.Default.Star, color = XpBadgeGold, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- RENDIMIENTO TÁCTICO (AMPLIADO) ---
        Text("RENDIMIENTO TÁCTICO SEMANAL", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        // Fila 1: Velocidades
        Row(Modifier.fillMaxWidth()) {
            AnalysisCard(
                title = "VELOCIDAD REAL",
                value = "${"%.2f".format(actualRate)} kg/sem",
                subValue = "Tu ritmo actual",
                color = if (actualRate >= requiredRate) XpBarGreen else Color.Red,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AnalysisCard(
                title = "OBJETIVO SEMANAL",
                value = "${"%.2f".format(requiredRate)} kg/sem",
                subValue = "Necesario para cumplir",
                color = RpgNeonCyan,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Fila 2: Proyección y Desviación
        Row(Modifier.fillMaxWidth()) {
            AnalysisCard(
                title = "DEBERÍAS PESAR HOY",
                value = "${"%.1f".format(expectedWeightToday)} kg",
                subValue = "Según el plan ideal",
                color = Color.LightGray,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            val diffText = if(abs(diff) < 0.1) "¡Exacto!" else "${"%.1f".format(abs(diff))} kg"
            val diffLabel = if (status == ChallengeStatus.AHEAD) "¡Vas Ganando!" else if (status == ChallengeStatus.BEHIND) "Debes recuperar" else "En punto"

            AnalysisCard(
                title = "DESVIACIÓN",
                value = diffText,
                subValue = diffLabel,
                color = status.color,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- PLAN DE VUELO (10 FASES DETALLADAS) ---
        Text("PLAN DE VUELO: 10 FASES DE EJECUCIÓN", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        Card(colors = CardDefaults.cardColors(containerColor = RpgPanel)) {
            Column(modifier = Modifier.padding(16.dp)) {

                // INICIO
                MilestoneRowDetailed("DESPLIEGUE (INICIO)", GameUtils.formatDate(startDate), startWeight, true, Color.Gray)
                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))

                // GENERAR 9 FASES INTERMEDIAS (10%, 20%... 90%)
                for (i in 1..9) {
                    val pct = i / 10f
                    val milestoneMillis = startDate + (totalDurationMillis * pct).toLong()
                    val milestoneWeight = startWeight - (totalDeltaWeight * pct)
                    val isPassed = progress >= pct

                    val phaseName = "FASE $i (${(pct*100).toInt()}%)"
                    val color = if (isPassed) RpgNeonCyan else Color.DarkGray

                    MilestoneRowDetailed(phaseName, GameUtils.formatDate(milestoneMillis), milestoneWeight, isPassed, RpgNeonCyan)
                    Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 12.dp))
                }

                // FINAL
                MilestoneRowDetailed("MISIÓN CUMPLIDA (100%)", GameUtils.formatDate(deadline), targetWeight, progress >= 1f, XpBadgeGold, isTarget = true)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// --- COMPONENTES VISUALES ---

@Composable
fun InfoBox(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier.height(70.dp),
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp, textAlign = TextAlign.Center)
            Text(label, color = Color.Gray, fontSize = 7.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun AnalysisCard(title: String, value: String, subValue: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = RpgPanel),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, color = Color.Gray, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(subValue, color = color, fontSize = 10.sp)
        }
    }
}

@Composable
fun MilestoneRowDetailed(phase: String, date: String, targetWeight: Float, isPassed: Boolean, color: Color, isTarget: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                imageVector = if (isTarget) Icons.Default.Star else if (isPassed) Icons.Default.CheckCircle else Icons.Default.Refresh,
                contentDescription = null,
                tint = if (isPassed || isTarget) color else Color.DarkGray,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(phase, color = if (isPassed || isTarget) Color.White else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(date, color = if (isPassed || isTarget) color else Color.DarkGray, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("OBJETIVO", color = Color.DarkGray, fontSize = 7.sp, fontWeight = FontWeight.Bold)
            Text("${"%.1f".format(targetWeight)} kg", color = if (isPassed || isTarget) Color.White else Color.Gray, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

enum class ChallengeStatus(val label: String, val color: Color, val icon: ImageVector) {
    AHEAD("ADELANTADO", Color(0xFF00E676), Icons.Default.Star),
    ON_TRACK("EN RANGO", RpgNeonCyan, Icons.Default.CheckCircle),
    BEHIND("RETRASADO", Color(0xFFFF5252), Icons.Default.Warning)
}