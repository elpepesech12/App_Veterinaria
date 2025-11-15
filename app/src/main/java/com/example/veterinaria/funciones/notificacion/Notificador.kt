package com.example.veterinaria.funciones.notificacion

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.veterinaria.MaturinApp
import com.example.veterinaria.R

object Notificador {

    // (se hace variable para que no se sobreescriban)
    private var contadorNotificacion = 0

    // notificacion simple pa probar
    fun enviarNotificacionSimple(
        contexto: Context,
        titulo: String,
        texto: String
    ) {

        // icono pa la noti
        val icono = R.drawable.ic_alertas

        // estructura de la noti
        val builder = NotificationCompat.Builder(contexto, MaturinApp.CANAL_ID_ALERTAS)
            .setSmallIcon(icono)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH) // prioridad alta
            .setAutoCancel(true) // ee cierra al tocarla

        // para cimprobar si la app tiene permiso ANTES de intentar enviarla
        if (ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // si no tiene permiso (porque se denegó) no hace nada
            return
        }

        // envía la notificación
        with(NotificationManagerCompat.from(contexto)) {
            // se usa una id diferente cada vez (contadorNotificacion++)
            // para que se muestren múltiples notificaciones y no se reemplacen
            notify(contadorNotificacion++, builder.build())
        }
    }
}
