package com.grogolden.mullet.ui.components.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

import com.grogolden.mullet.data.models.Mode
import com.grogolden.mullet.data.models.Task

/**
 * A dialog for editing an existing task.
 *
 * - Allows users to update the task title.
 * - Enables users to change the task's assigned mode.
 * - Provides options to **update** or **delete** the task.
 *
 * @param task The task being edited.
 * @param availableModes A list of available modes for selection.
 * @param onDismiss Callback triggered when the dialog is closed.
 * @param onTaskUpdated Callback triggered when the task is updated.
 * @param onTaskDeleted Callback triggered when the task is deleted.
 */
@Composable
fun EditTaskDialog(
    task: Task,
    availableModes: List<Mode>,
    onDismiss: () -> Unit,
    onTaskUpdated: (Task) -> Unit,
    onTaskDeleted: (Task) -> Unit,
) {
    var taskTitle by remember { mutableStateOf(task.title) }
    var currentMode by remember {
        mutableStateOf(availableModes.find { it.localId == task.modeId } ?: availableModes.first())
    }
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(IntSize.Zero) }

    val icon = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Task") },
        text = {
            Column {
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("Task Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Box {
                    // Mode selection dropdown
                    OutlinedTextField(
                        value = currentMode.title,
                        onValueChange = {},
                        label = { Text("Task Mode") },
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                icon,
                                contentDescription = "Dropdown Arrow",
                                modifier = Modifier.clickable { expanded = !expanded }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { coordinates ->
                                textFieldSize = coordinates.size
                            }
                            .clickable { expanded = true }
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    ) {
                        availableModes.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.title) },
                                onClick = {
                                    currentMode = mode
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Column {
                // Update Task Button
                Button(onClick = {
                    onTaskUpdated(
                        Task(
                            id = task.id,
                            localId = task.localId,
                            title = taskTitle,
                            modeId = currentMode.localId
                        )
                    )
                    onDismiss()
                }) {
                    Text("Update Task")
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Delete Task Button
                Button(
                    onClick = {
                        onTaskDeleted(task)
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Task", color = MaterialTheme.colorScheme.onError)
                }
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}





