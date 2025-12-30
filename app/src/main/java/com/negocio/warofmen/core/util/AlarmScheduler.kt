package com.negocio.warofmen.core.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.negocio.warofmen.core.receiver.DailyReminderReceiver
import java.util.Calendar

object AlarmScheduler {

    fun scheduleDailyReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // El Intent que dispara nuestro Receiver
        val intent = Intent(context, DailyReminderReceiver::class.java)

        // PendingIntent: Un permiso para ejecutar esto en el futuro
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001, // ID único para esta alarma
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Configurar la hora exacta
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Si la hora ya pasó hoy (ej: son las 8pm y puso alarma a las 6pm), lo agendamos para mañana
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        // Programar la alarma (Usamos setExact para ser precisos, o setInexactRepeating para ahorrar batería)
        // Para una app de hábitos, setRepeating es lo estándar.
        try {
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, // Despierta al celular si duerme
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY, // Repetir cada 24h
                pendingIntent
            )
        } catch (e: SecurityException) {
            // En Android 13+ a veces requiere permisos especiales si usas alarmas exactas,
            // pero setRepeating suele ser seguro.
            e.printStackTrace()
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, DailyReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
    }
}