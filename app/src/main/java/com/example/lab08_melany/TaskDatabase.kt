package com.example.lab08_melany

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Punto de acceso a la base de datos.
 * - entities: lista de tablas (entidades) que contiene.
 * - version: debe subir cuando cambie el esquema (y requerirá una migración).
 * - exportSchema = false: evita la advertencia de "schema export directory" en este laboratorio.
 */
@Database(entities = [Task::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    // Room implementa esta función y devuelve el DAO listo para usar
    abstract fun taskDao(): TaskDao
}
