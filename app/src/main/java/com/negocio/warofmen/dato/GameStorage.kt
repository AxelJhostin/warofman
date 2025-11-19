package com.negocio.warofmen.dato

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "game_settings")

class GameStorage(private val context: Context) {

    companion object {
        val NAME_KEY = stringPreferencesKey("user_name")
        val LEVEL_KEY = intPreferencesKey("user_level")
        val XP_KEY = intPreferencesKey("user_xp")
        val MAX_XP_KEY = intPreferencesKey("user_max_xp")
        val STR_KEY = intPreferencesKey("user_str")
        val STA_KEY = intPreferencesKey("user_sta")
        val GENDER_KEY = stringPreferencesKey("user_gender")
        val IS_CREATED_KEY = booleanPreferencesKey("is_created")

        // NUEVAS CLAVES
        val AGE_KEY = intPreferencesKey("user_age")
        val WEIGHT_KEY = floatPreferencesKey("user_weight")
        val HEIGHT_KEY = floatPreferencesKey("user_height")
        val BMI_KEY = floatPreferencesKey("user_bmi")
        val AGI_KEY = intPreferencesKey("user_agi") // Nuevo
        val WIL_KEY = intPreferencesKey("user_wil") // Nuevo
        val LUK_KEY = intPreferencesKey("user_luk")
        val WEIGHT_HISTORY_KEY = stringSetPreferencesKey("user_weight_history")
    }

    val getUserFlow: Flow<PlayerCharacter> = context.dataStore.data
        .map { preferences ->
            PlayerCharacter(
                name = preferences[NAME_KEY] ?: "",
                level = preferences[LEVEL_KEY] ?: 1,
                currentXp = preferences[XP_KEY] ?: 0,
                maxXp = preferences[MAX_XP_KEY] ?: 100,
                strength = preferences[STR_KEY] ?: 5,
                stamina = preferences[STA_KEY] ?: 5,
                agility = preferences[AGI_KEY] ?: 5,   // Lectura
                willpower = preferences[WIL_KEY] ?: 5, // Lectura
                luck = preferences[LUK_KEY] ?: 5,      // Lectura
                gender = preferences[GENDER_KEY] ?: "Guerrero",
                isCreated = preferences[IS_CREATED_KEY] ?: false,
                age = preferences[AGE_KEY] ?: 25,
                weight = preferences[WEIGHT_KEY] ?: 70f,
                height = preferences[HEIGHT_KEY] ?: 170f,
                bmi = preferences[BMI_KEY] ?: 24.2f,
                weightHistory = preferences[WEIGHT_HISTORY_KEY]?.toList() ?: emptyList()
            )
        }

    suspend fun savePlayer(player: PlayerCharacter) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = player.name
            preferences[LEVEL_KEY] = player.level
            preferences[XP_KEY] = player.currentXp
            preferences[MAX_XP_KEY] = player.maxXp
            preferences[GENDER_KEY] = player.gender
            preferences[IS_CREATED_KEY] = player.isCreated
            preferences[AGE_KEY] = player.age
            preferences[WEIGHT_KEY] = player.weight
            preferences[HEIGHT_KEY] = player.height
            preferences[BMI_KEY] = player.bmi
            preferences[STR_KEY] = player.strength
            preferences[STA_KEY] = player.stamina
            preferences[AGI_KEY] = player.agility
            preferences[WIL_KEY] = player.willpower
            preferences[LUK_KEY] = player.luck
            preferences[WEIGHT_HISTORY_KEY] = player.weightHistory.toSet()
        }
    }
}