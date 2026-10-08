// Autor: Melany Quispe Lope
// Laboratorio 08 - Programación en Móviles: Room + MVVM + UDF
package com.example.lab08_melany

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.room.Room
import com.example.lab08_melany.ui.theme.Lab08_melanyTheme

class MainActivity : ComponentActivity() {

    // Base de datos: se crea una sola vez (lazy) y NO en cada recomposición
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            TaskDatabase::class.java,
            "task_db"
        ).build()
    }

    // ViewModel creado con la fábrica: sobrevive a rotaciones de pantalla
    private val viewModel: TaskViewModel by viewModels {
        TaskViewModelFactory(database.taskDao())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab08_melanyTheme {
                // Scaffold entrega innerPadding para no quedar debajo de las barras del sistema
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TaskScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskScreen(viewModel: TaskViewModel, modifier: Modifier = Modifier) {
    // Observamos el StateFlow: cuando cambia, la UI se recompone sola (flujo unidireccional)
    val tasks by viewModel.tasks.collectAsState()

    // Estado local del campo de texto
    var newTaskDescription by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = "Lista de tareas",
            style = MaterialTheme.typography.headlineSmall
        )

        // Nombre de la autora en letra pequeña (identificación para el docente)
        Text(
            text = "Melany Quispe Lope",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo para escribir la nueva tarea
        TextField(
            value = newTaskDescription,
            onValueChange = { newTaskDescription = it },
            label = { Text("Nueva tarea") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Evento de usuario -> ViewModel
        Button(
            onClick = {
                if (newTaskDescription.isNotBlank()) {
                    viewModel.addTask(newTaskDescription.trim())
                    newTaskDescription = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Agregar tarea")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (tasks.isEmpty()) {
            // Estado vacío: se muestra cuando no hay tareas guardadas
            Text(
                text = "Aún no hay tareas. ¡Agrega la primera!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        } else {
            // Lista de tareas (LazyColumn permite hacer scroll si hay muchas)
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(tasks, key = { it.id }) { task ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = task.description,
                            // Tachado cuando la tarea está completada
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f)
                        )
                        // Evento de usuario: alternar completada/pendiente
                        Button(onClick = { viewModel.toggleTaskCompletion(task) }) {
                            Text(if (task.isCompleted) "Completada" else "Pendiente")
                        }
                    }
                    HorizontalDivider()
                }
            }
        }

        // Elimina todas las tareas (deshabilitado si la lista ya está vacía)
        Button(
            onClick = { viewModel.deleteAllTasks() },
            enabled = tasks.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Eliminar todas las tareas")
        }
    }
}
