package com.grogolden.mullet.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import javax.inject.Inject

import com.grogolden.mullet.data.models.Task
import com.grogolden.mullet.data.repository.TaskRepository

/**
 * Represents different UI states for the task list.
 */
sealed class TaskUiState {
    data object Loading : TaskUiState() // Represents a loading state
    data class Success(val tasks: List<Task>) : TaskUiState() // Represents successful data fetch
    data class Error(val message: String) : TaskUiState() // Represents an error state
}

/**
 * ViewModel for managing tasks in the Mullet app.
 *
 * - Fetches and manages task data.
 * - Handles adding, updating, and deleting tasks.
 * - Syncs unsynced tasks using WorkManager.
 *
 * @param repository Repository handling task-related data operations.
 * @param workManager WorkManager instance for background task synchronization.
 */
@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val workManager: WorkManager,
) : ViewModel() {

    // Holds the UI state for tasks (loading, success, or error)
    private val _uiState = MutableStateFlow<TaskUiState>(TaskUiState.Loading)
    val uiState: StateFlow<TaskUiState> = _uiState

    // Tracks whether a background sync is in progress
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        fetchTasks()
        observeSyncWorker() // Observes the status of the unsynced task sync worker
    }

    /**
     * Fetches tasks from the repository.
     *
     * - Syncs tasks with the remote database.
     * - Updates the local task list.
     * - Handles potential errors.
     */
    private fun fetchTasks() {
        viewModelScope.launch {
            try {
                repository.syncTasks()

                // Collect and update local task list
                repository.getAllTasks().collect { tasks ->
                    _uiState.value = TaskUiState.Success(tasks)
                }
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error fetching tasks: ${e.message}")
                _uiState.value = TaskUiState.Error("Error fetching tasks: ${e.message}")
            }
        }
    }

    /**
     * Adds a new task to the database.
     *
     * @param task The task to insert.
     */
    fun addTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.addTask(task)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error adding task: ${e.message}")
                _uiState.value = TaskUiState.Error("Error adding task: ${e.message}")
            }
        }
    }

    /**
     * Updates an existing task in the database.
     *
     * @param task The task with updated values.
     */
    fun updateTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.updateTask(task)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error updating task: ${e.message}")
                _uiState.value = TaskUiState.Error("Error updating task: ${e.message}")
            }
        }
    }

    /**
     * Marks a task as completed and deletes it after a short delay.
     *
     * @param task The task to mark as completed and remove.
     */
    fun deleteTaskAfterCompletion(task: Task) {
        viewModelScope.launch {
            // Update UI first: Mark task as completed in Room
            repository.updateTask(task.copy(isCompleted = true))

            // Short delay before deletion
            delay(500)

            // Delete the task
            repository.deleteTask(task)
        }
    }

    /**
     * Deletes a task from the database.
     *
     * @param task The task to remove.
     */
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            try {
                repository.deleteTask(task)
            } catch (e: Exception) {
                Log.e("TaskViewModel", "Error deleting task: ${e.message}")
                _uiState.value = TaskUiState.Error("Error deleting task: ${e.message}")
            }
        }
    }

    /**
     * Manually triggers a sync of tasks via WorkManager.
     */
    fun syncTasks() {
        viewModelScope.launch {
            repository.syncTasks()
        }
    }

    /**
     * Observes the status of the WorkManager sync job.
     *
     * Updates `_isSyncing` to `true` if the sync job is running.
     */
    private fun observeSyncWorker() {
        workManager.getWorkInfosForUniqueWorkLiveData("UNSYNCED_TASK_SYNC_WORK")
            .observeForever { workInfos ->
                _isSyncing.value = workInfos.any { it.state == WorkInfo.State.RUNNING }
            }
    }
}
