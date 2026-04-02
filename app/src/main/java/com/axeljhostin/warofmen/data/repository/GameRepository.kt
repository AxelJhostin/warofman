package com.axeljhostin.warofmen.data.repository

import android.content.Context
import com.axeljhostin.warofmen.data.model.PlayerCharacter
import com.axeljhostin.warofmen.data.source.GameStorage
import kotlinx.coroutines.flow.Flow

class GameRepository(context: Context) {

    // Le quitamos 'private' para que sea accesible desde los ViewModels
    val storage = GameStorage(context)

    val playerFlow: Flow<PlayerCharacter> = storage.getUserFlow

    suspend fun savePlayer(player: PlayerCharacter) {
        storage.savePlayer(player)
    }

    suspend fun resetGame() {
        storage.clearAllData()
    }
}