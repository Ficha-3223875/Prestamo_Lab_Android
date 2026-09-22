package com.example.prestamolab

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

class RecordatorioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val solicitudId = intent.getIntExtra(EXTRA_SOLICITUD_ID, -1)
        val equipo = intent.getStringExtra(EXTRA_EQUIPO) ?: "equipo"

        Notificaciones.crearCanal(context)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val contenido = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentIntent = PendingIntent.getActivity(
            context,
            solicitudId,
            contenido,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        val notificacion = NotificationCompat.Builder(context, Notificaciones.CANAL_RECORDATORIOS)
            .setSmallIcon(R.drawable.ic_stat_prestamolab)
            .setContentTitle("Recordatorio de devolución")
            .setContentText("El préstamo de $equipo debe devolverse hoy.")
            .setContentIntent(contentIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(solicitudId, notificacion)
    }

    companion object {
        const val EXTRA_SOLICITUD_ID = "extra_solicitud_id"
        const val EXTRA_EQUIPO = "extra_equipo"
    }
}

object Notificaciones {
    const val CANAL_RECORDATORIOS = "recordatorios_devolucion"

    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                CANAL_RECORDATORIOS,
                "Recordatorios de devolución",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Avisa cuándo debe devolverse un equipo prestado."
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}