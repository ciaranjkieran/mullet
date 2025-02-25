package com.grogolden.mullet.data.repository

import android.util.Log

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

import com.grogolden.mullet.background.tasks.DeleteTaskWorker
import com.grogolden.mullet.background.tasks.SyncTaskWorker

import com.grogolden.mullet.data.database.ModeDataSource
import com.grogolden.mullet.data.database.TaskDao

import com.grogolden.mullet.data.local.SyncStatusManager

import com.grogolden.mullet.data.mappers.TaskMapper

import com.grogolden.mullet.data.models.Task

import com.grogolden.mullet.data.network.api.TaskApiService

/**
 * Repository for managing task-related data operations.
 *
 * - Provides a single source of truth for tasks.
 * - Handles interactions between the local database, backend API, and background sync jobs.
 */
class TaskRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val taskApiService: TaskApiService,
    private val modeDataSource: ModeDataSource,
    private val workManager: WorkManager,
    private val syncStatusManager: SyncStatusManager, // Inject SyncStatusManager
) {

    /**
     * Observes all tasks from the local database in real-time.
     */
    fun getAllTasks(): Flow<List<Task>> =
        taskDao.getAllTasks().map { list -> list.map { TaskMapper.entityToTask(it) } }

    /**
     * Adds a new task locally and attempts to sync it with the backend.
     *
     * If the backend sync is successful, the task is updated with the assigned backend ID.
     */
    suspend fun addTask(task: Task): Task {
        val localTask = TaskMapper.taskToEntity(task).copy(isSynced = false)
        val insertedId = taskDao.insertTask(localTask)

        return try {
            val existingModes = modeDataSource.getAllModesOnce()
            val createdTaskDto = taskApiService.createTask(TaskMapper.toDto(task, existingModes))

            val createdTask = TaskMapper.fromDto(createdTaskDto, existingModes)
            val syncedTask = createdTask.copy(localId = insertedId, isSynced = true)

            taskDao.updateTask(TaskMapper.taskToEntity(syncedTask))
            syncedTask
        } catch (e: Exception) {
            e.printStackTrace()
            TaskMapper.entityToTask(taskDao.getTaskByLocalId(insertedId))
        }
    }

    /**
     * Updates an existing task both locally and on the backend.
     *
     * If the backend update is successful, the task is marked as synced.
     */
    suspend fun updateTask(task: Task) {
        if (task.id == null) {
            Log.e("TaskRepository", "Cannot update task, task has no backend ID. Task: $task")
            return
        }

        val localTask = TaskMapper.taskToEntity(task).copy(isSynced = false)
        taskDao.updateTask(localTask)

        try {
            val existingModes = modeDataSource.getAllModesOnce()
            val backendId = task.id

            val response = taskApiService.updateTask(
                backendId, TaskMapper.toDto(task, existingModes)
            )

            val updatedTask = TaskMapper.fromDto(response, existingModes)
            val syncedTask = updatedTask.copy(isSynced = true)

            taskDao.updateTask(TaskMapper.taskToEntity(syncedTask))
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error updating task: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Marks a task as deleted and schedules deletion with the backend.
     */
    suspend fun deleteTask(task: Task) {
        val localTask = task.copy(isDeleted = true, isSynced = false)
        taskDao.updateTask(TaskMapper.taskToEntity(localTask))
        scheduleTaskDeletion()
    }

    /**
     * Syncs local tasks with the backend.
     *
     * Ensures that tasks are fetched from the backend only once.
     * Subsequent syncs are handled by background jobs.
     */
    suspend fun syncTasks() {
        if (!syncStatusManager.hasSyncedTasks()) {
            fetchTasksFromBackendAndCache()
            syncStatusManager.setTasksSynced(true)
        } else {
            scheduleTaskSync()
        }
    }

    /**
     * Fetches tasks from the backend and caches new entries locally.
     */
    private suspend fun fetchTasksFromBackendAndCache() {
        try {
            val remoteTasks = taskApiService.getTasks()
            var existingModes = modeDataSource.getAllModesOnce()

            if (existingModes.isEmpty() && !syncStatusManager.hasSyncedModes()) {
                modeDataSource.fetchModesFromBackendAndCache()
                existingModes = modeDataSource.getAllModesOnce()
                syncStatusManager.setModesSynced(true)
            }

            val taskEntities =
                remoteTasks.map { TaskMapper.taskToEntity(TaskMapper.fromDto(it, existingModes)) }
            val existingModeIds = existingModes.map { it.localId }.toSet()

            val validTasks = taskEntities.filter { it.modeId in existingModeIds }

            if (validTasks.isNotEmpty()) {
                taskDao.insertTasks(validTasks)
            } else {
                Log.e("TaskRepository", "No valid tasks found for insertion")
            }
        } catch (e: Exception) {
            Log.e("TaskRepository", "Error fetching tasks: ${e.message}")
        }
    }

    /**
     * Schedules a background sync job for syncing local tasks with the backend.
     */
    private fun scheduleTaskSync() {
        val workRequest = OneTimeWorkRequestBuilder<SyncTaskWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        workManager.enqueueUniqueWork("SYNC_TASK_WORK", ExistingWorkPolicy.KEEP, workRequest)
    }

    private fun scheduleTaskDeletion() {
        val workRequest = OneTimeWorkRequestBuilder<DeleteTaskWorker>().setConstraints(
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
        ).build()

        workManager.enqueueUniqueWork("DELETE_TASK_WORK", ExistingWorkPolicy.KEEP, workRequest)
    }

    /**
     * Syncs locally deleted tasks with the backend.
     *
     * If deletion is successful, the task is removed from the local database.
     */
    suspend fun syncDeletedTasks() {
        val deletedTaskEntities = taskDao.getDeletedTasks()
        val deletedTasks = deletedTaskEntities.map { TaskMapper.entityToTask(it) }

        for (task in deletedTasks) {
            try {
                task.id?.let { taskApiService.deleteTask(it) }
                taskDao.deleteMarkedTasks()
            } catch (e: Exception) {
                Log.e("TaskRepository", "Error syncing deleted tasks: ${e.message}")
            }
        }
    }
}
