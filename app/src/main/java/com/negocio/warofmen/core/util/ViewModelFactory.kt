package com.negocio.warofmen.core.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.negocio.warofmen.ui.viewmodel.CreationViewModel
import com.negocio.warofmen.ui.viewmodel.HomeViewModel
import com.negocio.warofmen.ui.viewmodel.SettingsViewModel

class GameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(CreationViewModel::class.java)) {
            return CreationViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}