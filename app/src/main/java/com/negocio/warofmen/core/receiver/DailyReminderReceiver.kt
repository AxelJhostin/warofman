package com.negocio.warofmen.core.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.negocio.warofmen.MainActivity
import com.negocio.warofmen.R

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Cuando suena la alarma, mostramos la notificación
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val channelId = "war_of_men_daily_reminder"

        // 1. Crear el Canal de Notificación (Obligatorio en Android 8.0+)
        // Sin esto, la notificación nunca aparecerá en teléfonos modernos.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorio de Entrenamiento"
            val descriptionText = "Avisos para mantener tu racha"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Registramos el canal en el sistema
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Definir qué pasa al tocar la notificación
        // Queremos que abra la MainActivity
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE // Importante por seguridad en Android 12+
        )

        // 3. Construir la Notificación Visual
        // Usamos un icono de sistema por defecto para no complicarnos por ahora
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Icono de relojito
            .setContentTitle("🔥 ¡Tu racha está en peligro!")
            .setContentText("No dejes que se apague el fuego. Entrena hoy para mantener tu progreso.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // Al tocarla, abre la app
            .setAutoCancel(true) // Al tocarla, desaparece de la barra

        // 4. Mostrar la notificación
        try {
            val notificationManager = NotificationManagerCompat.from(context)

            // Verificamos el permiso en tiempo de ejecución (para Android 13+)
            if (ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(1001, builder.build())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}