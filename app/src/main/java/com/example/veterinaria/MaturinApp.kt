package com.example.veterinaria

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class MaturinApp : Application() {

    // se define un id constante para el canal de las notis
    // para saber "por donde" enviar la noti
    companion object {
        const val CANAL_ID_ALERTAS = "alertas_veterinaria"
    }

    override fun onCreate() {
        super.onCreate()

        // se crea el canal
        crearCanalDeNotificaciones()
    }

    /**
     * Esta es la función que registra el "Canal" en el sistema Android.
     */
    private fun crearCanalDeNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // propiedades del canal (esto es lo que el usuario ve en
            //    los ajustes de notificaciones del celu)
            val nombre = "Alertas de animales"
            val descripcionTexto = "Notificaciones para animales y citas"
            val importancia = NotificationManager.IMPORTANCE_HIGH // que suene y aparezca

            // se crea el objeto canal
            val canal = NotificationChannel(CANAL_ID_ALERTAS, nombre, importancia).apply {
                description = descripcionTexto
            }

            // se registra el canal en el NotificationManager del celu
            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(canal)
        }
    }
}