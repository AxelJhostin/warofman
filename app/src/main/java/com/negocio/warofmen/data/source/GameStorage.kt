package com.negocio.warofmen.data.source // Ojo al paquete

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.data.model.PlayerCharacter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "game_settings_v2") // Cambié el nombre para "resetear" datos viejos y evitar crashes

class GameStorage(private val context: Context) {

    private val gson = Gson()

    companion object {
        // Claves Simples
        val NAME_KEY = stringPreferencesKey("user_name")
        val GENDER_KEY = stringPreferencesKey("user_gender")
        val AGE_KEY = intPreferencesKey("user_age")
        val HEIGHT_KEY = floatPreferencesKey("user_height")
        val IS_CREATED_KEY = booleanPreferencesKey("is_created")

        // Claves de Estado Actual
        val WEIGHT_KEY = floatPreferencesKey("user_current_weight")
        val BMI_KEY = floatPreferencesKey("user_current_bmi")
        val BODY_FAT_KEY = floatPreferencesKey("user_current_fat") // Nuevo

        // Claves RPG
        val LEVEL_KEY = intPreferencesKey("user_level")
        val XP_KEY = intPreferencesKey("user_xp")
        val MAX_XP_KEY = intPreferencesKey("user_max_xp")
        val STR_KEY = intPreferencesKey("user_str")
        val STA_KEY = intPreferencesKey("user_sta")
        val AGI_KEY = intPreferencesKey("user_agi")
        val WIL_KEY = intPreferencesKey("user_wil")
        val LUK_KEY = intPreferencesKey("user_luk")

        // Claves COMPLEJAS (Listas JSON)
        val MEASUREMENT_LOGS_KEY = stringPreferencesKey("json_measurement_logs") // Guardaremos el JSON aquí
        val WORKOUT_LOGS_KEY = stringSetPreferencesKey("set_workout_logs") // Este lo dejamos simple por ahora
        val INVENTORY_KEY = stringSetPreferencesKey("set_inventory")

        val STREAK_KEY = intPreferencesKey("user_streak")
        val LAST_WORKOUT_KEY = longPreferencesKey("user_last_workout")
    }

    val getUserFlow: Flow<PlayerCharacter> = context.dataStore.data
        .map { preferences ->

            // LÓGICA DE DESERIALIZACIÓN (JSON -> Lista de Objetos)
            val jsonMeasurements = preferences[MEASUREMENT_LOGS_KEY]
            val measurementList: List<BodyLog> = if (jsonMeasurements != null) {
                val type = object : TypeToken<List<BodyLog>>() {}.type
                gson.fromJson(jsonMeasurements, type)
            } else {
                emptyList()
            }

            PlayerCharacter(
                name = preferences[NAME_KEY] ?: "",
                gender = preferences[GENDER_KEY] ?: "Guerrero",
                age = preferences[AGE_KEY] ?: 25,
                height = preferences[HEIGHT_KEY] ?: 170f,
                isCreated = preferences[IS_CREATED_KEY] ?: false,

                currentWeight = preferences[WEIGHT_KEY] ?: 70f,
                currentBmi = preferences[BMI_KEY] ?: 24.2f,
                currentBodyFat = preferences[BODY_FAT_KEY], // Puede ser null

                level = preferences[LEVEL_KEY] ?: 1,
                currentXp = preferences[XP_KEY] ?: 0,
                maxXp = preferences[MAX_XP_KEY] ?: 100,
                strength = preferences[STR_KEY] ?: 5,
                stamina = preferences[STA_KEY] ?: 5,
                agility = preferences[AGI_KEY] ?: 5,
                willpower = preferences[WIL_KEY] ?: 5,
                luck = preferences[LUK_KEY] ?: 5,

                // Asignamos la lista recuperada del JSON
                measurementLogs = measurementList,

                workoutLogs = preferences[WORKOUT_LOGS_KEY]?.toList() ?: emptyList(),
                inventory = preferences[INVENTORY_KEY]?.toList() ?: emptyList(),
                currentStreak = preferences[STREAK_KEY] ?: 0,
                lastWorkoutDate = preferences[LAST_WORKOUT_KEY] ?: 0L
            )
        }

    suspend fun savePlayer(player: PlayerCharacter) {
        context.dataStore.edit { preferences ->
            preferences[NAME_KEY] = player.name
            preferences[GENDER_KEY] = player.gender
            preferences[AGE_KEY] = player.age
            preferences[HEIGHT_KEY] = player.height
            preferences[IS_CREATED_KEY] = player.isCreated

            preferences[WEIGHT_KEY] = player.currentWeight
            preferences[BMI_KEY] = player.currentBmi
            if (player.currentBodyFat != null) {
                preferences[BODY_FAT_KEY] = player.currentBodyFat
            }

            preferences[LEVEL_KEY] = player.level
            preferences[XP_KEY] = player.currentXp
            preferences[MAX_XP_KEY] = player.maxXp
            preferences[STR_KEY] = player.strength
            preferences[STA_KEY] = player.stamina
            preferences[AGI_KEY] = player.agility
            preferences[WIL_KEY] = player.willpower
            preferences[LUK_KEY] = player.luck

            // LÓGICA DE SERIALIZACIÓN (Lista de Objetos -> JSON String)
            val jsonMeasurements = gson.toJson(player.measurementLogs)
            preferences[MEASUREMENT_LOGS_KEY] = jsonMeasurements

            preferences[WORKOUT_LOGS_KEY] = player.workoutLogs.toSet()
            preferences[INVENTORY_KEY] = player.inventory.toSet()

            preferences[STREAK_KEY] = player.currentStreak
            preferences[LAST_WORKOUT_KEY] = player.lastWorkoutDate
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear() // ¡Borrón y cuenta nueva!
        }
    }
}