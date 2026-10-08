package com.example.lab08_melany

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel: intermediario entre la UI y Room (arquitectura MVVM + UDF).
 * - El ESTADO fluye hacia la UI a través de [tasks].
 * - Los EVENTOS del usuario llegan desde la UI mediante las funciones públicas.
 */
class TaskViewModel(private val dao: TaskDao) : ViewModel() {

    // Estado mutable PRIVADO: solo el ViewModel puede modificarlo
    private val _tasks = MutableStateFlow<List<Task>>(emptyList())

    // Estado de SOLO LECTURA que la UI observa
    val tasks: StateFlow<List<Task>> = _tasks

    init {
        // Al inicializar, cargamos las tareas guardadas en la base de datos
        loadTasks()
    }

    // Lee la BD y publica el resultado en el estado
    private fun loadTasks() {
        viewModelScope.launch {
            _tasks.value = dao.getAllTasks()
        }
    }

    // Función para añadir una nueva tarea
    fun addTask(description: String) {
        val newTask = Task(description = description)
        viewModelScope.launch {
            dao.insertTask(newTask)
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    // Función para alternar el estado de completado de una tarea
    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            // copy() crea una nueva Task con el mismo id pero con el estado invertido
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            dao.updateTask(updatedTask)
            _tasks.value = dao.getAllTasks() // Recargamos la lista
        }
    }

    // Función para eliminar todas las tareas
    fun deleteAllTasks() {
        viewModelScope.launch {
            dao.deleteAllTasks()
            _tasks.value = emptyList() // Vaciamos la lista en el estado
        }
    }
}
