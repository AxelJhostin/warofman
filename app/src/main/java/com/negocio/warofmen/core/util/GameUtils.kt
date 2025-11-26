package com.negocio.warofmen.core.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.log10

object GameUtils {

    // ==========================================
    // 1. FORMATO Y TEXTO
    // ==========================================

    // Formatear fecha (Ej: "25 Nov")
    fun formatDate(millis: Long): String {
        val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
        return formatter.format(Date(millis))
    }

    // Extraer segundos de la descripción de una misión (Ej: "Plancha 30 seg" -> 30)
    fun extractSeconds(description: String): Int {
        val number = description.filter { it.isDigit() }
        return if (number.isNotEmpty()) number.toInt() else 10
    }

    // ==========================================
    // 2. FEEDBACK SENSORIAL (VIBRACIÓN)
    // ==========================================

    fun vibrate(context: Context, type: String) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= 26) {
            when (type) {
                "success" -> vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
                "levelup" -> {
                    // Patrón de vibración doble para subida de nivel
                    val timings = longArrayOf(0, 100, 100, 300)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                }
            }
        } else {
            // Soporte para versiones antiguas de Android
            vibrator.vibrate(200)
        }
    }

    // ==========================================
    // 3. FÓRMULAS BIOMÉTRICAS (MATEMÁTICAS)
    // ==========================================

    /**
     * Calcula el Índice de Masa Corporal (IMC)
     * Peso en Kg / (Altura en metros)^2
     */
    fun calculateBMI(weightKg: Float, heightCm: Float): Float {
        val heightMeters = heightCm / 100
        return if (heightMeters > 0) weightKg / (heightMeters * heightMeters) else 0f
    }

    /**
     * Calcula el Porcentaje de Grasa Corporal
     * Método de la Marina de EE.UU. (US Navy Method)
     *
     * @param gender "Guerrero" (Hombre) o "Amazona" (Mujer)
     * @return El porcentaje o null si faltan datos obligatorios (Cuello/Cintura)
     */
    fun calculateBodyFat(
        gender: String,
        heightCm: Float,
        neckCm: Float?,
        waistCm: Float?,
        hipCm: Float?
    ): Float? {
        // Validaciones básicas: Si falta cuello o cintura, no se puede calcular
        if (neckCm == null || waistCm == null) return null
        if (heightCm <= 0) return null

        return try {
            if (gender == "Guerrero" || gender == "H" || gender == "Hombre") {
                // Fórmula Hombres
                // 495 / (1.0324 - 0.19077(log10(cintura-cuello)) + 0.15456(log10(altura))) - 450
                val value1 = waistCm - neckCm
                if (value1 <= 0) return null // Evitar logaritmo de negativo

                val logWaistNeck = log10(value1)
                val logHeight = log10(heightCm)

                (495 / (1.0324 - 0.19077 * logWaistNeck + 0.15456 * logHeight)) - 450

            } else {
                // Fórmula Mujeres (Requiere cadera obligatoriamente)
                if (hipCm == null) return null

                // 495 / (1.29579 - 0.35004(log10(cintura+cadera-cuello)) + 0.22100(log10(altura))) - 450
                val value1 = waistCm + hipCm - neckCm
                if (value1 <= 0) return null

                val logWaistHipNeck = log10(value1)
                val logHeight = log10(heightCm)

                (495 / (1.29579 - 0.35004 * logWaistHipNeck + 0.22100 * logHeight)) - 450
            }.toFloat()

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}