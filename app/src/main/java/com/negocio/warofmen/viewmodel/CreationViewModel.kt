package com.negocio.warofmen.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.dato.GameRepository
import com.negocio.warofmen.dato.PlayerCharacter
import kotlinx.coroutines.launch

class CreationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    fun createCharacter(name: String, weight: Float, height: Float, age: Int, gender: String, onResult: () -> Unit) {
        val heightMeters = height / 100
        val bmi = if (heightMeters > 0) weight / (heightMeters * heightMeters) else 0f

        // Stats Base
        var baseStr = 5; var baseSta = 5; var baseAgi = 5; var baseWil = 5; var baseLuk = 5

        // Modificadores (Igual que antes)
        if (gender == "H") { baseStr += 2; baseSta += 1 } else { baseAgi += 2; baseWil += 1 }
        if (bmi > 25) { baseStr += 3; baseAgi -= 2 } else if (bmi < 20) { baseAgi += 3; baseStr -= 1 }
        if (age < 25) { baseSta += 2; baseWil -= 1 } else if (age > 35) { baseWil += 3; baseAgi -= 1 }

        val initialHistory = listOf("${System.currentTimeMillis()}:$weight")

        val newPlayer = PlayerCharacter(
            name = name,
            strength = baseStr.coerceAtLeast(1),
            stamina = baseSta.coerceAtLeast(1),
            agility = baseAgi.coerceAtLeast(1),
            willpower = baseWil.coerceAtLeast(1),
            luck = baseLuk,
            gender = if (gender == "H") "Guerrero" else "Amazona",
            isCreated = true,
            age = age,
            weight = weight,
            height = height,
            bmi = bmi,
            weightHistory = initialHistory
        )

        viewModelScope.launch {
            repository.savePlayer(newPlayer)
            onResult() // Avisamos que terminó para navegar
        }
    }
}