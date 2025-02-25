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
 * A **WorkManager** worker responsible for syncing tasks with the backend.
 *
 * - Ensures locally created/updated tasks are pushed to the backend.
 * - Uses **Hilt** for dependency injection.
 * - Implements retry logic to handle failures.
 *
 * @param context The application context.
 * @param workerParameters WorkManager-specific parameters.
 * @param taskRepository The repository responsible for task synchronization.
 */
@HiltWorker
class SyncTaskWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val taskRepository: TaskRepository,
) : CoroutineWorker(context, workerParameters) {

    /**
     * Performs the background sync operation.
     *
     * - Calls `taskRepository.syncTasks()` to push unsynced tasks to the backend.
     * - Retries the operation if an exception occurs (e.g., network failure).
     *
     * @return [Result.success] if sync completes successfully.
     * @return [Result.retry] if an error occurs.
     */
    override suspend fun doWork(): Result {
        return try {
            taskRepository.syncTasks()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    /**
     * Factory interface for creating instances of [SyncTaskWorker].
     *
     * - Used by Hilt and WorkManager for dependency injection.
     */
    @AssistedFactory
    interface Factory : ChildWorkerFactory<SyncTaskWorker> {
        override fun create(appContext: Context, workerParameters: WorkerParameters): SyncTaskWorker
    }
}
