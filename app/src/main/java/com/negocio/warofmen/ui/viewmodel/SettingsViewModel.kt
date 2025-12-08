package com.negocio.warofmen.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.negocio.warofmen.core.util.NotificationScheduler
import com.negocio.warofmen.data.repository.GameRepository
import com.negocio.warofmen.data.source.NotificationSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository(application)
    // Acceso directo al storage (en una app más grande, esto iría en el Repository)
    private val storage = repository.storage

    // Estado de la UI
    private val _notificationState = MutableStateFlow(NotificationSettings())
    val notificationState: StateFlow<NotificationSettings> = _notificationState.asStateFlow()

    init {
        // Cargar configuración guardada al iniciar
        viewModelScope.launch {
            storage.getNotificationSettings.collectLatest { settings ->
                _notificationState.value = settings
            }
        }
    }

    // Función para actualizar TODO (Switch y Hora)
    fun updateNotifications(isEnabled: Boolean, hour: Int, minute: Int) {
        viewModelScope.launch {
            // 1. Guardar en disco
            storage.saveNotificationSettings(isEnabled, hour, minute)

            // 2. Programar o Cancelar la alarma real
            val context = getApplication<Application>().applicationContext
            NotificationScheduler.scheduleDailyReminder(context, isEnabled, hour, minute)
        }
    }

    fun resetProgress(onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.resetGame()
            onSuccess()
        }
    }
}