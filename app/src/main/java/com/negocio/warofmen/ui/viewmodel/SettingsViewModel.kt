package com.negocio.warofmen.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.data.repository.GameRepository
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)

    fun resetProgress(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.resetGame()
            onSuccess() // Avisamos a la pantalla que ya se borró todo
        }
    }
}