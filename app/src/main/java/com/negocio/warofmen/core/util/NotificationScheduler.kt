package com.negocio.warofmen.core.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.negocio.warofmen.core.receiver.DailyReminderReceiver
import java.util.Calendar

object NotificationScheduler {

    private const val REMINDER_REQUEST_CODE = 1001

    // AHORA ACEPTA HORA Y MINUTOS
    fun scheduleDailyReminder(context: Context, isEnabled: Boolean, hour: Int = 18, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REMINDER_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (isEnabled) {
            val calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, hour) // Usamos la hora elegida
                set(Calendar.MINUTE, minute)    // Usamos el minuto elegido
                set(Calendar.SECOND, 0)
            }

            // Si la hora ya pasó hoy, programar para mañana
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_YEAR, 1)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )

            Log.d("NotificationScheduler", "✅ Alarma programada a las $hour:$minute")

        } else {
            alarmManager.cancel(pendingIntent)
            Log.d("NotificationScheduler", "❌ Alarma cancelada")
        }
    }
}