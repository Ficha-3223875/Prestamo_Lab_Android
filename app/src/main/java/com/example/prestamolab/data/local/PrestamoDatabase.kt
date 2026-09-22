package com.example.prestamolab.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [EquipoEntity::class, SolicitudEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PrestamoDatabase : RoomDatabase() {

    abstract fun equipoDao(): EquipoDao

    abstract fun solicitudDao(): SolicitudDao

    companion object {
        const val NOMBRE = "prestamolab.db"

        /**
         * Punto de extensión para migraciones incrementalmente versionadas (Semana 6).
         * Si en el futuro cambia el esquema, se agrega un Migration de N a N+1:
         * val MIGRATION_1_2 = object : Migration(1, 2) { ... }
         * y se añade a MIGRACIONES.
         */
        val MIGRACIONES: Array<Migration> = arrayOf()

        fun crear(context: Context): PrestamoDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                PrestamoDatabase::class.java,
                NOMBRE
            )
                .addMigrations(*MIGRACIONES)
                .fallbackToDestructiveMigration()
                .addCallback(DATOS_INICIALES)
                .build()

        private val DATOS_INICIALES = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Catálogo semilla: solo se carga la primera vez que se crea la base.
                val seed = listOf(
                    "INSERT INTO equipos (id, nombre, categoria, estado) VALUES (1, 'Portátil Lenovo ThinkPad', 'COMPUTO', 'DISPONIBLE')",
                    "INSERT INTO equipos (id, nombre, categoria, estado) VALUES (2, 'Video Beam Epson', 'AUDIOVISUAL', 'DISPONIBLE')",
                    "INSERT INTO equipos (id, nombre, categoria, estado) VALUES (3, 'Kit Arduino UNO', 'ELECTRONICA', 'DISPONIBLE')",
                    "INSERT INTO equipos (id, nombre, categoria, estado) VALUES (4, 'Router TP-Link', 'REDES', 'RESERVADO')",
                    "INSERT INTO equipos (id, nombre, categoria, estado) VALUES (5, 'Portátil HP ProBook', 'COMPUTO', 'PRESTADO')",
                    "INSERT INTO equipos (id, nombre, categoria, estado) VALUES (6, 'Cámara Logitech', 'AUDIOVISUAL', 'DISPONIBLE')"
                )
                db.beginTransaction()
                try {
                    seed.forEach { db.execSQL(it) }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
            }
        }
    }
}