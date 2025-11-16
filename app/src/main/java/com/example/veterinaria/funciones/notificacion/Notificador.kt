package com.example.veterinaria.funciones.notificacion

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.veterinaria.MaturinApp
import com.example.veterinaria.PantallasVet.CitasVet
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

    fun enviarNotificacionCita(
        contexto: Context,
        nombreAnimal: String,
        tipoCita: String,
        hora: String
    ) {
        val icono = R.drawable.ic_citas
        val titulo = "Nueva cita agendada"
        val texto = "$nombreAnimal ($tipoCita) - $hora"

        val intent = Intent(contexto, CitasVet::class.java).apply {
            // para abrir la app si estaba cerrada
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            contexto,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(contexto, MaturinApp.CANAL_ID_ALERTAS)
            .setSmallIcon(icono)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        // comprueba permiso y envía
        if (ActivityCompat.checkSelfPermission(
                contexto,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        with(NotificationManagerCompat.from(contexto)) {
            notify(contadorNotificacion++, builder.build())
        }
    }
}
