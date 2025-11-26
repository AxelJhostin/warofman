package com.negocio.warofmen.data.repository

import android.content.Context
import com.negocio.warofmen.data.source.GameStorage
import com.negocio.warofmen.data.model.PlayerCharacter
import kotlinx.coroutines.flow.Flow

// Esta clase es la ÚNICA verdad sobre los datos. Los ViewModels le pedirán info a ella.
class GameRepository(context: Context) {

    private val storage = GameStorage(context)

    // Obtener el usuario (como flujo de datos en tiempo real)
    val playerFlow: Flow<PlayerCharacter> = storage.getUserFlow

    // Guardar el usuario
    suspend fun savePlayer(player: PlayerCharacter) {
        storage.savePlayer(player)
    }
}