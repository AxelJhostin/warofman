package com.negocio.warofmen.ui.screens.creation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.ui.theme.*

// --- PASO 1: IDENTIDAD ---
@Composable
fun StepIdentity(
    nombre: String, onNameChange: (String) -> Unit,
    genero: String, onGenderChange: (String) -> Unit,
    edad: String, onAgeChange: (String) -> Unit,
    colors: TextFieldColors
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("CREDENCIALES", style = MaterialTheme.typography.titleMedium, color = RpgNeonCyan, letterSpacing = 2.sp)
        Text("Ingrese sus datos de agente", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Tarjeta de Inputs
        Card(
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = onNameChange,
                    label = { Text("Nombre Clave (Alias)") },
                    placeholder = { Text("Ej: Lobo Solitario") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = colors,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = edad,
                    onValueChange = { if (it.all { c -> c.isDigit() } && it.length < 3) onAgeChange(it) },
                    label = { Text("Edad Operativa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = colors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("ARQUETIPO DE COMBATE", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Selector de Clase Mejorado
        Row(Modifier.fillMaxWidth()) {
            ClassSelectionCardNew(
                title = "GUERRERO",
                subtitle = "Fuerza Bruta",
                desc = "+2 Fuerza\nIdeal para ganar masa.",
                icon = Icons.Default.Person,
                isSelected = genero == "H",
                color = StatStrength,
                onClick = { onGenderChange("H") },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            )
            ClassSelectionCardNew(
                title = "AMAZONA",
                subtitle = "Agilidad Pura",
                desc = "+2 Agilidad\nDefinición y tono.",
                icon = Icons.Default.Face,
                isSelected = genero == "M",
                color = StatAgility,
                onClick = { onGenderChange("M") },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            )
        }
    }
}

// --- PASO 2: CUERPO BÁSICO ---
@Composable
fun StepBodyBasic(
    peso: String, onWeightChange: (String) -> Unit,
    altura: String, onHeightChange: (String) -> Unit,
    colors: TextFieldColors
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("CALIBRACIÓN BIOMÉTRICA", style = MaterialTheme.typography.titleMedium, color = RpgNeonCyan, letterSpacing = 2.sp)
        Text("Estableciendo parámetros base", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = peso, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length < 6) onWeightChange(it) },
                        label = { Text("Peso (kg)") }, modifier = Modifier.weight(1f).padding(end=8.dp), colors = colors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = altura, onValueChange = { if (it.all { c -> c.isDigit() } && it.length < 4) onHeightChange(it) },
                        label = { Text("Altura (cm)") }, modifier = Modifier.weight(1f).padding(start=8.dp), colors = colors,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CÁLCULO EN TIEMPO REAL (Scanner Effect)
        val w = peso.toFloatOrNull() ?: 0f
        val h = altura.toFloatOrNull() ?: 0f
        val bmi = GameUtils.calculateBMI(w, h)

        if (bmi > 0) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, RpgNeonCyan.copy(alpha = 0.5f)),
                modifier = Modifier.animateContentSize()
            ) {
                Column(
                    Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = RpgNeonCyan)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("ANÁLISIS DE MASA CORPORAL (IMC)", color = Color.Gray, fontSize = 10.sp, letterSpacing = 1.sp)
                    Text("%.1f".format(bmi), fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)

                    val (status, color) = when {
                        bmi < 18.5 -> "BAJO PESO" to StatAgility
                        bmi < 25 -> "PESO NORMAL" to XpBarGreen
                        bmi < 30 -> "SOBREPESO" to StatStamina
                        else -> "OBESIDAD" to StatStrength
                    }

                    Surface(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text(status, color = color, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp)
                    }
                }
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("ESCANEO DE PRECISIÓN", style = MaterialTheme.typography.titleMedium, color = RpgNeonCyan, letterSpacing = 2.sp)
        Text("Cálculo de grasa corporal real", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        // Switch tipo "Toggle" Tech
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onHasTapeChange(!hasTape) }
                .background(if(hasTape) RpgNeonCyan.copy(alpha = 0.2f) else Color.Transparent)
                .padding(12.dp)
        ) {
            Checkbox(
                checked = hasTape,
                onCheckedChange = null, // Manejado por el Row
                colors = CheckboxDefaults.colors(checkedColor = RpgNeonCyan, checkmarkColor = Color.Black)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text("TENGO CINTA MÉTRICA", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Habilitar modo avanzado", color = Color.Gray, fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (hasTape) {
            Card(
                colors = CardDefaults.cardColors(containerColor = RpgPanel),
                border = BorderStroke(1.dp, Color.DarkGray.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = cuello, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length < 5) onNeckChange(it) },
                        label = { Text("Cuello (cm)") }, modifier = Modifier.fillMaxWidth(), colors = colors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = cintura, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length < 5) onWaistChange(it) },
                        label = { Text("Cintura (cm)") }, placeholder = { Text("A la altura del ombligo") },
                        modifier = Modifier.fillMaxWidth(), colors = colors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true
                    )

                    if (genero == "M" || genero == "Amazona") {
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = cadera, onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length < 5) onHipChange(it) },
                            label = { Text("Cadera (cm)") }, modifier = Modifier.fillMaxWidth(), colors = colors, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RESULTADO
            val h = alturaStr.toFloatOrNull() ?: 0f
            val n = cuello.toFloatOrNull()
            val c = cintura.toFloatOrNull()
            val hip = cadera.toFloatOrNull()
            val fat = GameUtils.calculateBodyFat(genero, h, n, c, hip)

            if (fat != null) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, RpgNeonCyan)
                ) {
                    Column(Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("PORCENTAJE DE GRASA", color = RpgNeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("%.1f%%".format(fat), fontSize = 48.sp, fontWeight = FontWeight.Black, color = Color.White)
                        Text("Método US Navy (Alta Precisión)", color = Color.Gray, fontSize = 10.sp)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Se usará el IMC para la estimación de atributos iniciales.\nPuedes actualizar estas medidas más tarde en Ajustes.",
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp
                )
            }
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
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("CONFIRMACIÓN DE AGENTE", style = MaterialTheme.typography.titleMedium, color = RpgNeonCyan, letterSpacing = 2.sp)
        Text("Revise los datos antes de iniciar", color = Color.Gray, fontSize = 12.sp)

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = RpgPanel),
            border = BorderStroke(1.dp, RpgNeonCyan.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(Modifier.padding(24.dp).fillMaxWidth()) {
                // SECCIÓN 1: IDENTIDAD
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(RpgNeonCyan, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("IDENTIDAD", color = RpgNeonCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(nombre.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 20.sp)
                Text("Nivel 1 • $genero", color = Color.LightGray, fontSize = 14.sp)

                Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 16.dp))

                // SECCIÓN 2: CUERPO
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(XpBarGreen, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ESTADO FÍSICO", color = XpBarGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatResumen("PESO", "$peso kg")
                    StatResumen("ALTURA", "$altura cm")
                    StatResumen("EDAD", "$edad años")
                }

                Spacer(modifier = Modifier.height(16.dp))

                val n = cuello.toFloatOrNull()
                if (n != null) {
                    Surface(color = RpgNeonCyan.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                        Text("DATOS AVANZADOS: REGISTRADOS", color = RpgNeonCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(6.dp))
                    }
                } else {
                    Text("Datos avanzados: Pendientes", color = Color.Gray, fontSize = 10.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onConfirm,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = RpgNeonCyan),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("¡INICIAR MISIÓN!", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 16.sp, letterSpacing = 1.sp)
        }
    }
}

// --- COMPONENTES AUXILIARES ---

@Composable
fun ClassSelectionCardNew(
    title: String, subtitle: String, desc: String, icon: ImageVector,
    isSelected: Boolean, color: Color, onClick: () -> Unit, modifier: Modifier
) {
    Card(
        modifier = modifier
            .height(160.dp) // Altura fija para uniformidad
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) color.copy(alpha = 0.15f) else RpgPanel),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) color else Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (isSelected) color else Color.Gray, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = if (isSelected) Color.White else Color.LightGray, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(subtitle, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(desc, color = Color.Gray, fontSize = 9.sp, textAlign = TextAlign.Center, lineHeight = 11.sp)
        }
    }
}

@Composable
fun StatResumen(label: String, value: String) {
    Column {
        Text(label, color = Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}