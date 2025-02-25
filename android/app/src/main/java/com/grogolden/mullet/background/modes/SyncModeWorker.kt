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
 * A **WorkManager** worker responsible for syncing modes with the backend.
 *
 * - Ensures local modes are kept in sync with the backend.
 * - Uses **Hilt** for dependency injection.
 * - Implements retry logic in case of failures.
 *
 * @param context The application context.
 * @param workerParameters WorkManager-specific parameters.
 * @param modeRepository The repository handling mode synchronization.
 */
@HiltWorker
class SyncModeWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val modeRepository: ModeRepository,
) : CoroutineWorker(context, workerParameters) {

    /**
     * Performs the background sync operation.
     *
     * - Calls `modeRepository.syncModes()` to sync unsynced modes.
     * - Retries the operation if an exception occurs (e.g., network failure).
     *
     * @return [Result.success] if sync completes successfully.
     * @return [Result.retry] if an error occurs.
     */
    override suspend fun doWork(): Result {
        return try {
            modeRepository.syncModes()
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    /**
     * Factory interface for creating instances of [SyncModeWorker].
     *
     * - Used by Hilt and WorkManager for dependency injection.
     */
    @AssistedFactory
    interface Factory : ChildWorkerFactory<SyncModeWorker> {
        override fun create(appContext: Context, workerParameters: WorkerParameters): SyncModeWorker
    }
}
