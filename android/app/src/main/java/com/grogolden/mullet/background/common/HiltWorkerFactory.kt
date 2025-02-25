package com.grogolden.mullet.background.common

import android.content.Context

import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters

import javax.inject.Inject
import javax.inject.Provider

/**
 * Custom WorkManager worker factory integrated with Hilt for dependency injection.
 *
 * - Dynamically resolves and injects dependencies into workers.
 * - Uses **ChildWorkerFactory** to create worker instances.
 *
 * @property workerFactories A map of worker classes to their corresponding factories.
 */
class HiltWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out ListenableWorker>, @JvmSuppressWildcards Provider<ChildWorkerFactory<out ListenableWorker>>>,
) : WorkerFactory() {

    /**
     * Creates a worker instance using dependency injection.
     *
     * - Dynamically identifies the correct factory for the given worker class.
     * - Throws an exception if the worker class is unknown.
     *
     * @param appContext The application context.
     * @param workerClassName The fully qualified class name of the worker.
     * @param workerParameters The parameters required to configure the worker.
     * @return A fully instantiated and injected worker.
     * @throws IllegalArgumentException If the worker class is not registered in the factory map.
     */
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters,
    ): ListenableWorker {
        val workerClass = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)

        // Find the corresponding factory for the requested worker class
        val foundEntry = workerFactories.entries.find { workerClass.isAssignableFrom(it.key) }
        val factoryProvider = foundEntry?.value
            ?: throw IllegalArgumentException("Unknown worker class: $workerClassName")

        return factoryProvider.get().create(appContext, workerParameters)
    }
}

