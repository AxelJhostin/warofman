package com.negocio.warofmen.data.source

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.negocio.warofmen.data.model.BodyLog
import com.negocio.warofmen.data.model.PlayerCharacter
import com.negocio.warofmen.data.model.WorkoutLog // <--- Importante: Nueva importación
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Instancia única del DataStore
val Context.dataStore by preferencesDataStore(name = "game_settings_v2")

class GameStorage(private val context: Context) {

    private val gson = Gson()

    companion object {
        // --- Claves de Identidad ---
        val NAME_KEY = stringPreferencesKey("user_name")
        val GENDER_KEY = stringPreferencesKey("user_gender")
        val AGE_KEY = intPreferencesKey("user_age")
        val HEIGHT_KEY = floatPreferencesKey("user_height")
        val IS_CREATED_KEY = booleanPreferencesKey("is_created")

        // --- Claves de Estado Físico ---
        val WEIGHT_KEY = floatPreferencesKey("user_current_weight")
        val BMI_KEY = floatPreferencesKey("user_current_bmi")
        val BODY_FAT_KEY = floatPreferencesKey("user_current_fat")

        // --- Claves RPG ---
        val LEVEL_KEY = intPreferencesKey("user_level")
        val XP_KEY = intPreferencesKey("user_xp")
        val MAX_XP_KEY = intPreferencesKey("user_max_xp")
        val STR_KEY = intPreferencesKey("user_str")
        val STA_KEY = intPreferencesKey("user_sta")
        val AGI_KEY = intPreferencesKey("user_agi")
        val WIL_KEY = intPreferencesKey("user_wil")
        val LUK_KEY = intPreferencesKey("user_luk")

        // --- Claves Complejas (Listas) ---
        val MEASUREMENT_LOGS_KEY = stringPreferencesKey("json_measurement_logs")

        // CAMBIO PRINCIPAL: Usamos una nueva clave String para guardar JSON,
        // en lugar del antiguo StringSet que daba problemas.
        val WORKOUT_LOGS_JSON_KEY = stringPreferencesKey("json_workout_logs_v2")

        val INVENTORY_KEY = stringSetPreferencesKey("set_inventory")

        // --- Claves de Racha (Streak) ---
        val STREAK_KEY = intPreferencesKey("user_streak")
        val LAST_WORKOUT_KEY = longPreferencesKey("user_last_workout")

        // --- Claves de Notificaciones ---
        val NOTIF_ENABLED_KEY = booleanPreferencesKey("notif_enabled")
        val NOTIF_HOUR_KEY = intPreferencesKey("notif_hour")
        val NOTIF_MINUTE_KEY = intPreferencesKey("notif_minute")
    }

    // -------------------------------------------------------------------------
    // 1. FLUJO DEL JUGADOR (Lectura Constante)
    // -------------------------------------------------------------------------
    val getUserFlow: Flow<PlayerCharacter> = context.dataStore.data
        .map { preferences ->

            // Deserializar historial de peso
            val jsonMeasurements = preferences[MEASUREMENT_LOGS_KEY]
            val measurementList: List<BodyLog> = if (jsonMeasurements != null) {
                val type = object : TypeToken<List<BodyLog>>() {}.type
                gson.fromJson(jsonMeasurements, type)
            } else {
                emptyList()
            }

            // CAMBIO: Deserializar historial de entrenamientos (Objetos WorkoutLog)
            val jsonWorkouts = preferences[WORKOUT_LOGS_JSON_KEY]
            val workoutList: List<WorkoutLog> = if (jsonWorkouts != null) {
                val type = object : TypeToken<List<WorkoutLog>>() {}.type
                gson.fromJson(jsonWorkouts, type)
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
                currentBodyFat = preferences[BODY_FAT_KEY],

                level = preferences[LEVEL_KEY] ?: 1,
                currentXp = preferences[XP_KEY] ?: 0,
                maxXp = preferences[MAX_XP_KEY] ?: 100,
                strength = preferences[STR_KEY] ?: 5,
                stamina = preferences[STA_KEY] ?: 5,
                agility = preferences[AGI_KEY] ?: 5,
                willpower = preferences[WIL_KEY] ?: 5,
                luck = preferences[LUK_KEY] ?: 5,

                measurementLogs = measurementList,

                // Asignamos la lista de objetos reales
                workoutLogs = workoutList,

                inventory = preferences[INVENTORY_KEY]?.toList() ?: emptyList(),

                // Racha
                currentStreak = preferences[STREAK_KEY] ?: 0,
                lastWorkoutDate = preferences[LAST_WORKOUT_KEY] ?: 0L
            )
        }

    // -------------------------------------------------------------------------
    // 2. GUARDAR JUGADOR
    // -------------------------------------------------------------------------
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

            val jsonMeasurements = gson.toJson(player.measurementLogs)
            preferences[MEASUREMENT_LOGS_KEY] = jsonMeasurements

            // CAMBIO: Serializar la lista de WorkoutLog a JSON
            val jsonWorkouts = gson.toJson(player.workoutLogs)
            preferences[WORKOUT_LOGS_JSON_KEY] = jsonWorkouts

            preferences[INVENTORY_KEY] = player.inventory.toSet()

            preferences[STREAK_KEY] = player.currentStreak
            preferences[LAST_WORKOUT_KEY] = player.lastWorkoutDate
        }
    }

    // -------------------------------------------------------------------------
    // 3. GESTIÓN DE NOTIFICACIONES
    // -------------------------------------------------------------------------

    // Leer configuración
    val getNotificationSettings: Flow<NotificationSettings> = context.dataStore.data
        .map { preferences ->
            NotificationSettings(
                isEnabled = preferences[NOTIF_ENABLED_KEY] ?: false,
                hour = preferences[NOTIF_HOUR_KEY] ?: 18, // Por defecto 18:00
                minute = preferences[NOTIF_MINUTE_KEY] ?: 0
            )
        }

    // Guardar configuración
    suspend fun saveNotificationSettings(isEnabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit { preferences ->
            preferences[NOTIF_ENABLED_KEY] = isEnabled
            preferences[NOTIF_HOUR_KEY] = hour
            preferences[NOTIF_MINUTE_KEY] = minute
        }
    }

    // -------------------------------------------------------------------------
    // 4. RESET TOTAL
    // -------------------------------------------------------------------------
    suspend fun clearAllData() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

// Clase de datos auxiliar
data class NotificationSettings(
    val isEnabled: Boolean = false,
    val hour: Int = 18,
    val minute: Int = 0
)