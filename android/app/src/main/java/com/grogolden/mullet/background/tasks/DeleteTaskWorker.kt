package com.grogolden.mullet.background.tasks

import android.content.Context

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import androidx.hilt.work.HiltWorker

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

import com.grogolden.mullet.background.common.ChildWorkerFactory
import com.grogolden.mullet.data.repository.TaskRepository

/**
 * A **WorkManager** worker that handles the deletion of tasks from the backend.
 *
 * - Syncs locally deleted tasks with the backend.
 * - Uses **Hilt** for dependency injection.
 * - Implements retry logic for failure handling.
 *
 * @param context The application context.
 * @param workerParameters WorkManager-specific parameters.
 * @param taskRepository The repository responsible for syncing task deletions.
 */
@HiltWorker
class DeleteTaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(context, workerParameters) {

    /**
     * Executes the task deletion sync in the background.
     *
     * - Calls `taskRepository.syncDeletedTasks()` to delete unsynced tasks.
     * - Retries the operation if an exception occurs (e.g., network failure).
     *
     * @return [Result.success] if the sync completes successfully.
     * @return [Result.retry] if an error occurs.
     */
    override suspend fun doWork(): Result {
        return try {
            taskRepository.syncDeletedTasks()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    /**
     * Factory interface for creating instances of [DeleteTaskWorker].
     *
     * - Used by Hilt and WorkManager for dependency injection.
     */
    @AssistedFactory
    interface Factory : ChildWorkerFactory<DeleteTaskWorker> {
        override fun create(
            appContext: Context,
            workerParameters: WorkerParameters,
        ): DeleteTaskWorker
    }
}
