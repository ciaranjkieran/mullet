package com.grogolden.mullet.data.di

import androidx.work.ListenableWorker

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

import javax.inject.Provider
import javax.inject.Singleton

import com.grogolden.mullet.background.common.ChildWorkerFactory
import com.grogolden.mullet.background.modes.DeleteModeWorker
import com.grogolden.mullet.background.modes.SyncModeWorker
import com.grogolden.mullet.background.tasks.DeleteTaskWorker
import com.grogolden.mullet.background.tasks.SyncTaskWorker


@Module
@InstallIn(SingletonComponent::class)
abstract class WorkerModule {

    // Mode Workers
    @Binds
    @IntoMap
    @WorkerKey(SyncModeWorker::class)
    abstract fun bindSyncModeWorker(factory: SyncModeWorker.Factory): ChildWorkerFactory<SyncModeWorker>

    @Binds
    @IntoMap
    @WorkerKey(DeleteModeWorker::class)
    abstract fun bindDeleteModeWorker(factory: DeleteModeWorker.Factory): ChildWorkerFactory<DeleteModeWorker>

    // Task Workers
    @Binds
    @IntoMap
    @WorkerKey(SyncTaskWorker::class)
    abstract fun bindSyncTaskWorker(factory: SyncTaskWorker.Factory): ChildWorkerFactory<SyncTaskWorker>

    @Binds
    @IntoMap
    @WorkerKey(DeleteTaskWorker::class)
    abstract fun bindDeleteTaskWorker(factory: DeleteTaskWorker.Factory): ChildWorkerFactory<DeleteTaskWorker>

    companion object {
        @Provides
        @Singleton
        fun provideWorkerFactory(
            workerFactories: @JvmSuppressWildcards Map<Class<out ListenableWorker>, Provider<ChildWorkerFactory<out ListenableWorker>>>
        ): com.grogolden.mullet.background.common.HiltWorkerFactory {
            return com.grogolden.mullet.background.common.HiltWorkerFactory(workerFactories)
        }
    }
}
