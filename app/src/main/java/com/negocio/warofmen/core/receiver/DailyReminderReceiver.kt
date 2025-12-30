package com.negocio.warofmen.core.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.negocio.warofmen.MainActivity
import com.negocio.warofmen.R
import kotlin.random.Random // <--- Necesario para la aleatoriedad

class DailyReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        showNotification(context)
    }

    private fun showNotification(context: Context) {
        val channelId = "war_of_men_daily_reminder"

        // 1. Crear Canal con PRIORIDAD ALTA
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Recordatorio de Entrenamiento"
            val descriptionText = "Avisos épicos para mantener tu racha"
            // CAMBIO: Importancia ALTA para que suene/vibre
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Intent para abrir la App
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // 3. FRASES RPG ALEATORIAS (El "Sabor")
        val titles = listOf(
            "⚔️ ¡El deber te llama!",
            "🔥 Tu racha está en peligro",
            "🛡️ El reino necesita fuerza",
            "⚡ No te detengas ahora",
            "💀 La pereza es el enemigo"
        )
        val messages = listOf(
            "Un día sin entrenar es un día perdido. ¡Levántate!",
            "Tus estadísticas no subirán solas. Ve a entrenar.",
            "La disciplina separa a los guerreros de los plebeyos.",
            "Solo toma unos minutos. Hazlo por tu honor.",
            "Tu personaje te espera para subir de nivel."
        )

        // Elegimos una al azar
        val randomTitle = titles[Random.nextInt(titles.size)]
        val randomMessage = messages[Random.nextInt(messages.size)]

        // 4. Construir la Notificación
        val builder = NotificationCompat.Builder(context, channelId)
            // CAMBIO: Usamos el icono de tu app (asegúrate que exista mipmap o drawable)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(randomTitle) // Título aleatorio
            .setContentText(randomMessage) // Mensaje aleatorio
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // 5. Mostrar (Tu lógica de seguridad estaba perfecta)
        try {
            val notificationManager = NotificationManagerCompat.from(context)

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