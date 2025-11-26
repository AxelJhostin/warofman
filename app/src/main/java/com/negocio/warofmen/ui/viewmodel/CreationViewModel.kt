package com.negocio.warofmen.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.core.util.GameUtils
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.data.repository.GameRepository
import kotlinx.coroutines.launch

class CreationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    /**
     * Crea el personaje con todos los datos biométricos y calcula los stats iniciales.
     */
    fun createCharacter(
        name: String,
        gender: String, // "Guerrero" o "Amazona"
        age: Int,
        height: Float,
        weight: Float,
        // Datos Opcionales
        neck: Float? = null,
        waist: Float? = null,
        hip: Float? = null,
        onResult: () -> Unit
    ) {
        // 1. Cálculos Biométricos
        val bmi = GameUtils.calculateBMI(weight, height)
        val bodyFat = GameUtils.calculateBodyFat(gender, height, neck, waist, hip)

        // 2. Stats Base (Todos empiezan en 5)
        var baseStr = 5; var baseSta = 5; var baseAgi = 5; var baseWil = 5; var baseLuk = 5

        // 3. Modificadores por GÉNERO (Arquetipos)
        if (gender == "Guerrero" || gender == "H") {
            baseStr += 2
            baseSta += 1
        } else { // Amazona
            baseAgi += 2
            baseWil += 1
        }

        // 4. Modificadores por COMPOSICIÓN CORPORAL (Realidad vs RPG)
        // Si tenemos el dato de grasa, lo usamos (es más preciso). Si no, usamos IMC.
        if (bodyFat != null) {
            if (bodyFat < 15) { // Muy definido/atlético
                baseAgi += 3
                baseSta += 2
            } else if (bodyFat > 25) { // Con reservas de energía
                baseStr += 2 // Masa mueve masa
                baseAgi -= 1
            } else { // Promedio
                baseSta += 1
            }
        } else {
            // Fallback al IMC si no hay medidas de cinta métrica
            if (bmi > 25) { baseStr += 2; baseAgi -= 1 } // Posible sobrepeso o mucho músculo
            else if (bmi < 20) { baseAgi += 2; baseStr -= 1 } // Ligero
        }

        // 5. Modificadores por EDAD
        if (age < 25) { baseSta += 2; baseWil -= 1 } // Energía joven
        else if (age > 35) { baseWil += 3; baseAgi -= 1 } // Disciplina veterana

        // Asegurar mínimos
        baseStr = baseStr.coerceAtLeast(1)
        baseSta = baseSta.coerceAtLeast(1)
        baseAgi = baseAgi.coerceAtLeast(1)
        baseWil = baseWil.coerceAtLeast(1)

        // 6. Crear el primer registro del historial (Log Inicial)
        val initialLog = BodyLog(
            timestamp = System.currentTimeMillis(),
            weight = weight,
            neck = neck,
            waist = waist,
            hip = hip,
            bodyFatPercentage = bodyFat
        )

        // 7. Construir el Personaje
        val newPlayer = PlayerCharacter(
            name = name,
            gender = gender,
            age = age,
            height = height,
            isCreated = true,

            // Estado Actual
            currentWeight = weight,
            currentBmi = bmi,
            currentBodyFat = bodyFat,

            // Stats RPG Calculados
            strength = baseStr,
            stamina = baseSta,
            agility = baseAgi,
            willpower = baseWil,
            luck = baseLuk,

            // Historial
            measurementLogs = listOf(initialLog)
        )

        // 8. Guardar en Base de Datos
        viewModelScope.launch {
            repository.savePlayer(newPlayer)
            onResult()
        }
    }
}