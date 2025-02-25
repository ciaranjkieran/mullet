package com.grogolden.mullet.ui.components.tasks

import androidx.compose.foundation.layout.Column

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.grogolden.mullet.data.models.Task

/**
 * A dialog for adding a new task.
 *
 * - Allows users to enter a task title.
 * - Defaults new tasks to **Mullet Mode (modeId = 1)**.
 * - Ensures tasks are not added with an empty title.
 *
 * @param onDismiss Callback triggered when the dialog is closed.
 * @param onTaskAdded Callback triggered when a new task is successfully created.
 */
@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdded: (Task) -> Unit,
) {
    var taskTitle by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Task") },
        text = {
            Column {
                // Task title input field
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (taskTitle.isNotBlank()) {
                        val newTask = Task(
                            title = taskTitle,
                            modeId = 1, // Default to All Mode
                            id = null
                        )
                        onTaskAdded(newTask)
                        onDismiss()
                    }
                }
            ) {
                Text("Add Task")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
