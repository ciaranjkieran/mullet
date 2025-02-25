package com.grogolden.mullet.ui.screens

import android.util.Log

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel

import androidx.navigation.NavController

import com.grogolden.mullet.data.models.Task
import com.grogolden.mullet.ui.components.common.AddButton
import com.grogolden.mullet.ui.components.modes.ModeDialog
import com.grogolden.mullet.ui.components.modes.ModeSelector
import com.grogolden.mullet.ui.components.tasks.AddTaskDialog
import com.grogolden.mullet.ui.components.tasks.EditTaskDialog
import com.grogolden.mullet.ui.components.tasks.TaskCard
import com.grogolden.mullet.viewmodel.GlobalModeViewModel
import com.grogolden.mullet.viewmodel.TaskUiState
import com.grogolden.mullet.viewmodel.TaskViewModel

/**
 * The main dashboard screen of the Mullet app.
 *
 * - Displays tasks based on the selected mode.
 * - Allows users to add, edit, and delete tasks.
 * - Provides a mode selector for filtering tasks.
 * - Uses dialogs for adding/editing tasks and modes.
 *
 * @param navController The navigation controller for handling screen transitions.
 * @param globalModeViewModel ViewModel managing modes across the app.
 * @param taskViewModel ViewModel handling task-related operations.
 */
@Composable
fun DashboardScreen(
    navController: NavController,
    globalModeViewModel: GlobalModeViewModel = hiltViewModel(),
    taskViewModel: TaskViewModel = hiltViewModel(),
) {
    // Collecting state from ViewModels
    val taskUiState by taskViewModel.uiState.collectAsState()
    val selectedMode by globalModeViewModel.selectedMode.collectAsState()
    val availableModes by globalModeViewModel.modes.collectAsState()

    // UI states for dialogs and task editing
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var showTaskDialog by remember { mutableStateOf(false) } // State for task dialog
    var showModeDialog by remember { mutableStateOf(false) }
    var showAddModeDialog by remember { mutableStateOf(false) }

    // Filtering tasks based on the selected mode
    val filteredTasks = if (taskUiState is TaskUiState.Success) {
        (taskUiState as TaskUiState.Success).tasks.filter {
            selectedMode?.let { mode ->
                if (mode.id == 1) true // "All" mode shows all tasks
                else it.modeId == mode.localId
            } ?: true
        }
    } else {
        emptyList()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Mode Selector: Allows users to switch between modes
        ModeSelector(
            onEditMode = { showModeDialog = true },
            onDeleteMode = {
                selectedMode?.let { globalModeViewModel.deleteMode(it) }
            },
            onAddMode = { showAddModeDialog = true }
        )

        Spacer(modifier = Modifier.height(16.dp)) // Creates spacing between UI elements

        // Task List with Loading and Error Handling
        when (taskUiState) {
            is TaskUiState.Loading -> {
                // Show a loading spinner while tasks are being fetched
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            is TaskUiState.Success -> {
                // Display the list of tasks
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(filteredTasks) { task ->
                        TaskCard(
                            task = task,
                            availableModes = availableModes,
                            onTaskCheckedChange = { isChecked ->
                                if (isChecked) {
                                    taskViewModel.deleteTaskAfterCompletion(task) // Mark task complete
                                } else {
                                    taskViewModel.updateTask(task.copy(isCompleted = false)) // Revert to incomplete
                                }
                            },
                            onTaskClicked = { taskToEdit = task }, // Open task editor when clicked
                        )
                    }
                }
            }

            is TaskUiState.Error -> {
                // Show an error message if tasks fail to load
                Text(
                    text = (taskUiState as TaskUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

    }
    // Dialog for editing a selected task
    taskToEdit?.let { task ->
        EditTaskDialog(
            task = task,
            availableModes = availableModes, // Pass the list of modes
            onDismiss = { taskToEdit = null },
            onTaskUpdated = { updatedTask ->
                taskViewModel.updateTask(updatedTask)
                taskToEdit = null
            },
            onTaskDeleted = { taskToDelete ->
                taskViewModel.deleteTask(taskToDelete)
                taskToEdit = null
            }
        )
    }
    // Floating action button for adding tasks and modes
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        AddButton(
            onAddTask = { showTaskDialog = true },
            onAddMode = { showAddModeDialog = true }
        )
    }

    // Dialog for adding a new task
    if (showTaskDialog) {
        AddTaskDialog(
            onDismiss = { showTaskDialog = false },
            onTaskAdded = {
                val task = it.copy(id = null, modeId = selectedMode!!.localId)
                Log.d(
                    "AddTaskDialog",
                    "Adding task: $selectedMode, ${selectedMode!!.id}, ${task.modeId}"
                )
                taskViewModel.addTask(task)
            }
        )
    }

    // Dialog for editing the current mode
    if (showModeDialog) {
        ModeDialog(
            modeToEdit = selectedMode!!,
            onDismiss = { showModeDialog = false },
            onModeAdded = { /* No-op; this block isn’t used when editing */ },
            onModeUpdated = { updatedMode ->
                globalModeViewModel.updateMode(updatedMode)
                globalModeViewModel.selectMode(updatedMode)  // Ensure UI updates
                showModeDialog = false
            },
            onModeDeleted = { updatedMode ->
                globalModeViewModel.deleteMode(updatedMode)
            }
        )
    }

    // Dialog for adding a new mode
    if (showAddModeDialog) {
        ModeDialog(
            modeToEdit = null,
            onDismiss = { showAddModeDialog = false },
            onModeAdded = { newMode ->
                showAddModeDialog = false // Close the dialog immediately
                globalModeViewModel.addMode(newMode) { insertedMode ->
                    globalModeViewModel.selectMode(insertedMode) // Select the inserted mode
                }
            },
            onModeUpdated = { /* Not used in add mode */ },
            onModeDeleted = { /* No-op */ }
        )
    }
}
