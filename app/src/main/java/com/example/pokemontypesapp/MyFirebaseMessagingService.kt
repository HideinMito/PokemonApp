package com.example.pokemontypesapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val CHANNEL_ID = "pokemon_battle_channel"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: "Sin título"
        val body = remoteMessage.notification?.body ?: "Sin contenido"

        // Crear la notificación (sistema)
        createNotificationChannel(applicationContext)

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        // Mostrar la notificación en el sistema
        NotificationManagerCompat.from(applicationContext).notify(1, notification)

        // Enviar los datos a la MainActivity si está activa
        val intent = Intent("com.example.pokemontypesapp.NOTIFICATION")
        intent.putExtra("title", title)
        intent.putExtra("body", body)
        sendBroadcast(intent) // Enviar los datos como un Broadcast
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Pokemon Channel"
            val descriptionText = "Notifications for Pokemon battle results"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.description = descriptionText

            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
