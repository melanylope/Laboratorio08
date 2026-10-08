package com.example.lab08_melany

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * DAO (Data Access Object): define las operaciones permitidas sobre la tabla "tasks".
 * Room genera la implementación automáticamente en tiempo de compilación (gracias a KSP).
 * Todas las funciones son "suspend" para ejecutarse en segundo plano sin bloquear la UI.
 */
@Dao
interface TaskDao {

    // Obtener todas las tareas
    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<Task>
    // Insertar una nueva tarea
    @Insert
    suspend fun insertTask(task: Task)
    // Marcar una tarea como completada o no completada (Room busca la fila por su id)
    @Update
    suspend fun updateTask(task: Task)
    // Eliminar todas las tareas
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
