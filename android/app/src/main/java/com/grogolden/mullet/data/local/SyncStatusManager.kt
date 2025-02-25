package com.grogolden.mullet.data.local

import android.content.Context
import android.content.SharedPreferences

import dagger.hilt.android.qualifiers.ApplicationContext

import javax.inject.Inject
import javax.inject.Singleton

/**
 * **SyncStatusManager** - Manages sync status persistence using SharedPreferences.
 *
 * - Tracks whether tasks and modes have been synced with the backend.
 * - Provides utility methods to retrieve and update sync status.
 * - Used to prevent redundant sync operations.
 *
 * @param context The application context, injected via Hilt.
 */
@Singleton
class SyncStatusManager @Inject constructor(@ApplicationContext context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)

    /**
     * Checks if tasks have been synced with the backend.
     *
     * @return `true` if tasks have been synced, `false` otherwise.
     */
    fun hasSyncedTasks(): Boolean = prefs.getBoolean("has_synced_tasks", false)

    /**
     * Updates the sync status of tasks.
     *
     * @param synced `true` to mark tasks as synced, `false` otherwise.
     */
    fun setTasksSynced(synced: Boolean) {
        prefs.edit().putBoolean("has_synced_tasks", synced).apply()
    }

    /**
     * Checks if modes have been synced with the backend.
     *
     * @return `true` if modes have been synced, `false` otherwise.
     */
    fun hasSyncedModes(): Boolean = prefs.getBoolean("has_synced_modes", false)

    /**
     * Updates the sync status of modes.
     *
     * @param synced `true` to mark modes as synced, `false` otherwise.
     */
    fun setModesSynced(synced: Boolean) {
        prefs.edit().putBoolean("has_synced_modes", synced).apply()
    }
}
