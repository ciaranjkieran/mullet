package com.grogolden.mullet.data.di

import android.content.Context

import androidx.work.WorkManager

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import javax.inject.Singleton

import com.grogolden.mullet.data.database.ModeDao
import com.grogolden.mullet.data.database.ModeDataSource
import com.grogolden.mullet.data.database.MulletDatabase
import com.grogolden.mullet.data.database.TaskDao

import com.grogolden.mullet.data.local.SyncStatusManager

import com.grogolden.mullet.data.network.api.ModeApiService
import com.grogolden.mullet.data.network.api.TaskApiService

import com.grogolden.mullet.data.repository.ModeRepository
import com.grogolden.mullet.data.repository.TaskRepository

/**
 * Hilt Dependency Injection module for providing app-wide dependencies.
 *
 * - Provides networking, database, repository, and WorkManager dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides WorkManager for background task scheduling.
     */
    @Provides
    @Singleton
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    /**
     * Provides an OkHttpClient with logging enabled for debugging API requests.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    /**
     * Provides a Retrofit instance configured with the base API URL.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000/") // Backend URL for Android emulator
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides an instance of ModeApiService for making mode-related API calls.
     */
    @Provides
    @Singleton
    fun provideModeApiService(retrofit: Retrofit): ModeApiService {
        return retrofit.create(ModeApiService::class.java)
    }

    /**
     * Provides an instance of TaskApiService for making task-related API calls.
     */
    @Provides
    @Singleton
    fun provideTaskApiService(retrofit: Retrofit): TaskApiService {
        return retrofit.create(TaskApiService::class.java)
    }

    /**
     * Provides an instance of the Room database.
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MulletDatabase {
        return MulletDatabase.getDatabase(context)
    }

    /**
     * Provides a DAO for accessing mode-related database operations.
     */
    @Provides
    @Singleton
    fun provideModeDao(database: MulletDatabase): ModeDao {
        return database.modeDao()
    }

    /**
     * Provides a DAO for accessing task-related database operations.
     */
    @Provides
    @Singleton
    fun provideTaskDao(database: MulletDatabase): TaskDao {
        return database.taskDao()
    }

    /**
     * Provides an instance of SyncStatusManager to manage synchronization state.
     */
    @Provides
    @Singleton
    fun provideSyncStatusManager(@ApplicationContext context: Context): SyncStatusManager {
        return SyncStatusManager(context)
    }

    /**
     * Provides a ModeRepository for handling mode-related operations.
     */
    @Provides
    @Singleton
    fun provideModeRepository(
        modeDao: ModeDao,
        modeApiService: ModeApiService,
        workManager: WorkManager,
        syncStatusManager: SyncStatusManager, // ✅ Inject SyncStatusManager
    ): ModeRepository {
        return ModeRepository(modeDao, modeApiService, workManager, syncStatusManager)
    }

    /**
     * Provides ModeDataSource as a dependency for repositories.
     */
    @Provides
    @Singleton
    fun provideModeDataSource(modeRepository: ModeRepository): ModeDataSource {
        return modeRepository
    }

    /**
     * Provides a TaskRepository for handling task-related operations.
     */
    @Provides
    @Singleton
    fun provideTaskRepository(
        taskDao: TaskDao,
        taskApiService: TaskApiService,
        modeDataSource: ModeDataSource,
        workManager: WorkManager,
        syncStatusManager: SyncStatusManager, // Inject SyncStatusManager
    ): TaskRepository {
        return TaskRepository(
            taskDao,
            taskApiService,
            modeDataSource,
            workManager,
            syncStatusManager
        )
    }
}
