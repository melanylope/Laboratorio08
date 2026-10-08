package com.example.lab08_melany

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una tarea.
 * Cada instancia de Task es una FILA de la tabla "tasks" en la base de datos.
 */
@Entity(tableName = "tasks")
data class Task(
    // Clave primaria autogenerada: Room asigna 1, 2, 3... al insertar (por eso el valor por defecto es 0)
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Texto de la tarea; se guarda en la columna "description"
    @ColumnInfo(name = "description") val description: String,

    // Estado de la tarea; Room guarda el Boolean como INTEGER (0 = false, 1 = true)
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false
)
