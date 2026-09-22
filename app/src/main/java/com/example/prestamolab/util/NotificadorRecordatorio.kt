package com.example.prestamolab.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.prestamolab.RecordatorioReceiver

/**
 * Programa recordatorios de devolución (HU-09) usando AlarmManager.
 * El receptor publica una notificación cuando llega la hora límite.
 */
interface NotificadorRecordatorio {
    fun programar(solicitudId: Int, equipoNombre: String, fechaLimite: Long)
    fun cancelar(solicitudId: Int)

    companion object {
        val NoOp = object : NotificadorRecordatorio {
            override fun programar(solicitudId: Int, equipoNombre: String, fechaLimite: Long) = Unit
            override fun cancelar(solicitudId: Int) = Unit
        }
    }
}

class AlarmRecordatorio(private val context: Context) : NotificadorRecordatorio {

    override fun programar(solicitudId: Int, equipoNombre: String, fechaLimite: Long) {
        val intent = Intent(context, RecordatorioReceiver::class.java).apply {
            putExtra(RecordatorioReceiver.EXTRA_SOLICITUD_ID, solicitudId)
            putExtra(RecordatorioReceiver.EXTRA_EQUIPO, equipoNombre)
        }
        val pending = pendingIntent(solicitudId, intent)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // Inexacto: cumple el recordatorio sin exigir permisos especiales.
        alarmManager.set(AlarmManager.RTC_WAKEUP, fechaLimite, pending)
    }

    override fun cancelar(solicitudId: Int) {
        val intent = Intent(context, RecordatorioReceiver::class.java)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent(solicitudId, intent))
    }

    private fun pendingIntent(solicitudId: Int, intent: Intent): PendingIntent {
        val flags = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        return PendingIntent.getBroadcast(context, solicitudId, intent, flags)
    }
}