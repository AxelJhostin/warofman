package com.negocio.warofmen.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GameUtils {

    // Formatear fecha para la gráfica
    fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    // Parsear peso del historial "timestamp:peso"
    fun getWeightFromHistory(entry: String?): Float {
        if (entry == null) return 0f
        return entry.split(":")[1].toFloatOrNull() ?: 0f
    }

    // Extraer segundos de la descripción de la misión
    fun extractSeconds(description: String): Int {
        val number = description.filter { it.isDigit() }
        return if (number.isNotEmpty()) number.toInt() else 10
    }

    // Calcular IMC
    fun calculateBmi(weight: Float, height: Float): Float {
        val heightMeters = height / 100
        return if (heightMeters > 0) weight / (heightMeters * heightMeters) else 0f
    }
}