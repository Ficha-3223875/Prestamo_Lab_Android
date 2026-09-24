package com.example.prestamolab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

@Database(entities = [EquipoEntity::class, SolicitudEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class PrestamoDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao
    abstract fun solicitudDao(): SolicitudDao

    companion object {
        @Volatile
        private var INSTANCE: PrestamoDatabase? = null

        fun getInstance(context: Context): PrestamoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    PrestamoDatabase::class.java,
                    "prestamolab.db"
                )
                    .build()
                    .also { db ->
                        runBlocking(Dispatchers.IO) {
                            if (db.equipoDao().obtenerTodos().isEmpty()) {
                                db.equipoDao().insertarTodos(equiposSemilla())
                            }
                        }
                        INSTANCE = db
                    }
            }

        private fun equiposSemilla() = listOf(
            EquipoEntity(1, "Portátil Lenovo ThinkPad", "COMPUTO", "DISPONIBLE"),
            EquipoEntity(2, "Video Beam Epson", "AUDIOVISUAL", "DISPONIBLE"),
            EquipoEntity(3, "Kit Arduino UNO", "ELECTRONICA", "DISPONIBLE"),
            EquipoEntity(4, "Router TP-Link", "REDES", "DISPONIBLE"),
            EquipoEntity(5, "Portátil HP ProBook", "COMPUTO", "DISPONIBLE"),
            EquipoEntity(6, "Cámara Logitech", "AUDIOVISUAL", "DISPONIBLE")
        )
    }
}