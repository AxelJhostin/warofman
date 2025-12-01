package com.negocio.warofmen.ui.screens.creation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.ui.components.ClassSelectionCard
import com.negocio.warofmen.ui.theme.*

// --- PASO 1: IDENTIDAD ---
@Composable
fun StepIdentity(
    nombre: String, onNameChange: (String) -> Unit,
    genero: String, onGenderChange: (String) -> Unit,
    edad: String, onAgeChange: (String) -> Unit,
    colors: TextFieldColors
) {
    Text("¿QUIÉN ERES?", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(24.dp))

    OutlinedTextField(
        value = nombre,
        onValueChange = onNameChange,
        label = { Text("Nombre de Héroe") },
        modifier = Modifier.fillMaxWidth(),
        colors = colors,
        // Agregamos autoCorrect = false para evitar el crash del emulador
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences,
            autoCorrect = false,
            keyboardType = KeyboardType.Text
        )
    )
    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = edad, onValueChange = { if (it.all { c -> c.isDigit() }) onAgeChange(it) },
        label = { Text("Edad") }, modifier = Modifier.fillMaxWidth(), colors = colors,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
    )
    Spacer(modifier = Modifier.height(24.dp))

    Text("SELECCIONA CLASE", color = Color.Gray, fontSize = 12.sp)
    Spacer(modifier = Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth()) {
        ClassSelectionCard("GUERRERO", "+2 Fuerza\nIdeal para masa", Icons.Default.Person, genero == "H", StatStrength, { onGenderChange("H") }, Modifier.weight(1f).padding(end = 4.dp))
        ClassSelectionCard("AMAZONA", "+2 Agilidad\nDefinición y tono", Icons.Default.Face, genero == "M", StatAgility, { onGenderChange("M") }, Modifier.weight(1f).padding(start = 4.dp))
    }
}

// --- PASO 2: CUERPO BÁSICO ---
@Composable
fun StepBodyBasic(
    peso: String, onWeightChange: (String) -> Unit,
    altura: String, onHeightChange: (String) -> Unit,
    colors: TextFieldColors
) {
    Text("BIOMETRÍA BÁSICA", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
    Text("Datos necesarios para iniciar el sistema.", color = Color.Gray)
    Spacer(modifier = Modifier.height(24.dp))

    Row(Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = peso, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onWeightChange(it) },
            label = { Text("Peso (kg)") }, modifier = Modifier.weight(1f).padding(end=4.dp), colors = colors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        OutlinedTextField(
            value = altura, onValueChange = { if (it.all { c -> c.isDigit() }) onHeightChange(it) },
            label = { Text("Altura (cm)") }, modifier = Modifier.weight(1f).padding(start=4.dp), colors = colors,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
    }

    Spacer(modifier = Modifier.height(32.dp))

    val w = peso.toFloatOrNull() ?: 0f
    val h = altura.toFloatOrNull() ?: 0f
    val bmi = GameUtils.calculateBMI(w, h)

    if (bmi > 0) {
        Card(colors = CardDefaults.cardColors(containerColor = RpgPanel), border = androidx.compose.foundation.BorderStroke(1.dp, Color.DarkGray)) {
            Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TU ÍNDICE DE MASA CORPORAL (IMC)", color = Color.Gray, fontSize = 10.sp)
                Text("%.1f".format(bmi), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)

                val (status, color) = when {
                    bmi < 18.5 -> "BAJO PESO" to StatAgility
                    bmi < 25 -> "NORMAL" to XpBarGreen
                    bmi < 30 -> "SOBREPESO" to StatStamina
                    else -> "OBESIDAD" to StatStrength
                }
                Text(status, color = color, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// --- PASO 3: MEDIDAS AVANZADAS ---
@Composable
fun StepBodyAdvanced(
    genero: String,
    hasTape: Boolean, onHasTapeChange: (Boolean) -> Unit,
    cuello: String, onNeckChange: (String) -> Unit,
    cintura: String, onWaistChange: (String) -> Unit,
    cadera: String, onHipChange: (String) -> Unit,
    colors: TextFieldColors,
    alturaStr: String, pesoStr: String
) {
    Text("DATOS AVANZADOS", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
    Text("Calcula tu % de Grasa Real (Opcional)", color = Color.Gray)
    Spacer(modifier = Modifier.height(24.dp))

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onHasTapeChange(!hasTape) }) {
        Checkbox(checked = hasTape, onCheckedChange = onHasTapeChange, colors = CheckboxDefaults.colors(checkedColor = RpgNeonCyan))
        Text("Tengo una cinta métrica", color = Color.White)
    }

    if (hasTape) {
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(value = cuello, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onNeckChange(it) }, label = { Text("Cuello (cm)") }, modifier = Modifier.fillMaxWidth(), colors = colors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = cintura, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onWaistChange(it) }, label = { Text("Cintura (cm) - Ombligo") }, modifier = Modifier.fillMaxWidth(), colors = colors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))

        if (genero == "M" || genero == "Amazona") {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = cadera, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) onHipChange(it) }, label = { Text("Cadera (cm)") }, modifier = Modifier.fillMaxWidth(), colors = colors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
        }

        Spacer(modifier = Modifier.height(24.dp))

        val h = alturaStr.toFloatOrNull() ?: 0f
        val n = cuello.toFloatOrNull()
        val c = cintura.toFloatOrNull()
        val hip = cadera.toFloatOrNull()

        val fat = GameUtils.calculateBodyFat(genero, h, n, c, hip)

        if (fat != null) {
            Card(colors = CardDefaults.cardColors(containerColor = RpgPanel), border = androidx.compose.foundation.BorderStroke(1.dp, RpgNeonCyan)) {
                Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("GRASA CORPORAL ESTIMADA", color = RpgNeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("%.1f %%".format(fat), fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Método US Navy", color = Color.Gray, fontSize = 10.sp)
                }
            }
        }
    } else {
        Spacer(modifier = Modifier.height(32.dp))
        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
            Text("Puedes omitir este paso si no tienes cinta métrica.\nUsaremos tu IMC para calcular tus stats.", color = Color.Gray, textAlign = TextAlign.Center)
        }
    }
}

// --- PASO 4: RESUMEN ---
@Composable
fun StepSummary(
    nombre: String, genero: String, edad: String, peso: String, altura: String,
    cuello: String, cintura: String, cadera: String,
    onConfirm: () -> Unit
) {
    Text("CONFIRMAR HÉROE", style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(24.dp))

    Card(colors = CardDefaults.cardColors(containerColor = RpgPanel)) {
        Column(Modifier.padding(16.dp).fillMaxWidth()) {
            Text("IDENTIDAD", color = RpgNeonCyan, fontWeight = FontWeight.Bold)
            Text("$nombre | Nvl 1 $genero", color = Color.White)
            Divider(color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp))
            Text("CUERPO", color = RpgNeonCyan, fontWeight = FontWeight.Bold)
            Text("$peso kg | $altura cm | $edad años", color = Color.White)
            val n = cuello.toFloatOrNull()
            if (n != null) Text("Datos avanzados registrados", color = StatAgility, fontSize = 12.sp)
            else Text("Datos avanzados pendientes", color = Color.Gray, fontSize = 12.sp)
        }
    }
    Spacer(modifier = Modifier.height(40.dp))
    Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan)) {
        Text("¡COMENZAR AVENTURA!", color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun WizardProgressBar(currentStep: Int, totalSteps: Int) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        repeat(totalSteps) { index ->
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(if(index == currentStep) 24.dp else 8.dp)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(if (index <= currentStep) RpgNeonCyan else Color.DarkGray)
            )
        }
    }
}