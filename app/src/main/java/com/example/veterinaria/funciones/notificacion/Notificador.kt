package com.example.veterinaria.funciones.notificacion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.veterinaria.MaturinApp // ¡Asegúrate que sea el nombre de tu App class!
import com.example.veterinaria.R

object Notificador {

    // (Lo hacemos variable para que no se sobreescriban)
    private var contadorNotificacion = 0

    /**
     * Construye y envía una notificación simple.
     */
    fun enviarNotificacionSimple(
        contexto: Context,
        titulo: String,
        texto: String
    ) {

        // --- ¡EL ICONO ES OBLIGATORIO! ---
        // (Usa uno de los que ya tienes, como 'ic_alerta_critico' o 'ic_config')
        val icono = R.drawable.ic_alertas

        // 1. Construye la notificación usando el "Builder" nativo
        val builder = NotificationCompat.Builder(contexto, MaturinApp.CANAL_ID_ALERTAS)
            .setSmallIcon(icono)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // Prioridad alta
            .setAutoCancel(true) // Se cierra al tocarla

        // 2. Comprueba si la app tiene permiso ANTES de intentar enviarla
        // (Esto es una segunda verificación de seguridad)
        if (ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no tiene permiso (porque el usuario lo denegó), no hace nada.
            return
        }

        // 3. Envía la notificación
        with(NotificationManagerCompat.from(contexto)) {
            // Usamos un ID diferente cada vez (contadorNotificacion++)
            // para que se muestren múltiples notificaciones y no se reemplacen.
            notify(contadorNotificacion++, builder.build())
        }
    }
}
