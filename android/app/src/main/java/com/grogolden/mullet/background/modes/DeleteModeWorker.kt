package com.grogolden.mullet.background.modes

import android.content.Context

import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import androidx.hilt.work.HiltWorker

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

import com.grogolden.mullet.background.common.ChildWorkerFactory
import com.grogolden.mullet.data.repository.ModeRepository

/**
 * A **WorkManager** worker that handles the deletion of modes from the backend.
 *
 * - Syncs locally deleted modes with the backend.
 * - Uses Hilt dependency injection for seamless repository access.
 * - Implements retry logic on failure.
 *
 * @param context The application context.
 * @param workerParameters WorkManager-specific parameters.
 * @param modeRepository The repository responsible for syncing mode deletions.
 */
@HiltWorker
class DeleteModeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val modeRepository: ModeRepository,
) : CoroutineWorker(context, workerParameters) {

    /**
     * Executes the mode deletion sync in the background.
     *
     * - Calls `modeRepository.syncDeletedModes()` to delete unsynced modes.
     * - Retries the operation if an exception occurs.
     *
     * @return [Result.success] if the sync is successful.
     * @return [Result.retry] if an error occurs (e.g., network failure).
     */
    override suspend fun doWork(): Result {
        return try {
            modeRepository.syncDeletedModes()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    /**
     * Factory interface for creating instances of [DeleteModeWorker].
     *
     * - Used by Hilt and WorkManager for dependency injection.
     */
    @AssistedFactory
    interface Factory : ChildWorkerFactory<DeleteModeWorker> {
        override fun create(
            appContext: Context,
            workerParameters: WorkerParameters,
        ): DeleteModeWorker
    }
}
