package com.negocio.warofmen.core.util

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.negocio.warofmen.R

object SoundManager {

    private var soundPool: SoundPool? = null

    // IDs de los sonidos cargados
    private var soundClickId: Int = 0
    private var soundLevelUpId: Int = 0
    private var soundBeepId: Int = 0
    private var soundSuccessId: Int = 0

    // Bandera para saber si el usuario silenció la app (opcional para el futuro)
    private var isMuted = false

    fun init(context: Context) {
        if (soundPool == null) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            soundPool = SoundPool.Builder()
                .setMaxStreams(5) // Máximo 5 sonidos a la vez
                .setAudioAttributes(audioAttributes)
                .build()

            // CARGAR SONIDOS (Asegúrate de tener estos archivos en res/raw/)
            // Si no tienes los archivos aún, comenta estas líneas para que no de error
            try {
                // soundClickId = soundPool?.load(context, R.raw.sfx_click, 1) ?: 0
                // soundLevelUpId = soundPool?.load(context, R.raw.sfx_levelup, 1) ?: 0
                // soundBeepId = soundPool?.load(context, R.raw.sfx_timer_beep, 1) ?: 0
                // soundSuccessId = soundPool?.load(context, R.raw.sfx_success, 1) ?: 0
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playClick() {
        if (!isMuted && soundClickId != 0) {
            soundPool?.play(soundClickId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun playLevelUp() {
        if (!isMuted && soundLevelUpId != 0) {
            soundPool?.play(soundLevelUpId, 1f, 1f, 1, 0, 1f)
        }
    }

    fun playBeep() {
        if (!isMuted && soundBeepId != 0) {
            soundPool?.play(soundBeepId, 0.8f, 0.8f, 0, 0, 1f)
        }
    }

    fun playSuccess() {
        if (!isMuted && soundSuccessId != 0) {
            soundPool?.play(soundSuccessId, 1f, 1f, 0, 0, 1f)
        }
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}