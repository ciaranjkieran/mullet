package com.grogolden.mullet.background.common

import android.content.Context

import androidx.work.ListenableWorker
import androidx.work.WorkerParameters

/**
 * Generic factory interface for creating WorkManager workers.
 *
 * - Used to inject dependencies into custom **WorkManager** workers.
 * - Enables dynamic worker instantiation through dependency injection.
 *
 * @param T The type of `ListenableWorker` this factory creates.
 */
interface ChildWorkerFactory<T : ListenableWorker> {
    /**
     * Creates a new instance of the worker.
     *
     * @param appContext The application context.
     * @param workerParameters The parameters required to configure the worker.
     * @return A new instance of `T`, the specified worker type.
     */
    fun create(appContext: Context, workerParameters: WorkerParameters): T
}
