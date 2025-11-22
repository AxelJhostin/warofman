package com.negocio.warofmen.dato

import android.content.Context
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