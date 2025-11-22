package com.negocio.warofmen.util

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.negocio.warofmen.viewmodel.CreationViewModel
import com.negocio.warofmen.viewmodel.HomeViewModel

class GameViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        if (modelClass.isAssignableFrom(CreationViewModel::class.java)) {
            return CreationViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}