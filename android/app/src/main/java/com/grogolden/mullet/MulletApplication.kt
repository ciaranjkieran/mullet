package com.grogolden.mullet

import android.app.Application

import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration

import dagger.hilt.android.HiltAndroidApp

import javax.inject.Inject

/**
 * The main application class for the Mullet app.
 *
 * - Initializes Hilt for dependency injection.
 * - Configures WorkManager to use Hilt for worker dependency injection.
 */
@HiltAndroidApp
class MulletApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    /**
     * Configures WorkManager to use Hilt for injecting dependencies into workers.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}
