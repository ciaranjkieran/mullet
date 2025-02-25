package com.grogolden.mullet.data.repository

import android.util.Log

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

import com.grogolden.mullet.background.modes.DeleteModeWorker
import com.grogolden.mullet.background.modes.SyncModeWorker

import com.grogolden.mullet.data.database.ModeDao
import com.grogolden.mullet.data.database.ModeDataSource

import com.grogolden.mullet.data.local.SyncStatusManager
import com.grogolden.mullet.data.local.entities.ModeEntity

import com.grogolden.mullet.data.mappers.ModeMapper

import com.grogolden.mullet.data.models.Mode

import com.grogolden.mullet.data.network.api.ModeApiService

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

import javax.inject.Inject

/**
 * Repository for managing mode-related data operations.
 *
 * - Provides a single source of truth for modes.
 * - Handles interactions between the database, network, and background sync.
 * - Implements `ModeDataSource` for consistency.
 */
class ModeRepository @Inject constructor(
    private val modeDao: ModeDao,
    private val modeApiService: ModeApiService,
    private val workManager: WorkManager,
    private val syncStatusManager: SyncStatusManager,
) : ModeDataSource {

    /**
     * Retrieves all modes from the local database as a one-time fetch.
     *
     * @return A list of stored modes.
     */
    override suspend fun getAllModesOnce(): List<ModeEntity> {
        return modeDao.getAllModesOnce()
    }

    /**
     * Observes all modes from the local database in real-time.
     *
     * @return A Flow emitting a list of mapped `Mode` objects.
     */
    fun getAllModes(): Flow<List<Mode>> =
        modeDao.getAllModes().map { list -> list.map { ModeMapper.entityToMode(it) } }

    /**
     * Adds a new mode both locally and on the backend.
     *
     * - Inserts into Room first.
     * - Attempts to sync with the backend.
     * - Updates the local record with the backend ID after a successful sync.
     *
     * @param mode The mode to be added.
     * @return The successfully stored mode.
     */
    suspend fun addMode(mode: Mode): Mode {
        val localMode = ModeMapper.toEntity(mode).copy(isSynced = false)
        val insertedId = modeDao.insertMode(localMode)

        return try {
            val createdModeDto = modeApiService.createMode(ModeMapper.toDto(mode))
            val createdMode = ModeMapper.fromDto(createdModeDto)

            val syncedModeEntity = ModeMapper.toEntity(createdMode).copy(
                localId = insertedId,
                backendId = createdMode.id,
                isSynced = true
            )

            modeDao.updateMode(syncedModeEntity)

            ModeMapper.entityToMode(syncedModeEntity)
        } catch (e: Exception) {
            e.printStackTrace()
            ModeMapper.entityToMode(modeDao.getModeByLocalId(insertedId))
        }
    }

    /**
     * Updates a mode both locally and on the backend.
     *
     * - Updates Room first and marks as unsynced.
     * - Attempts to sync with the backend.
     * - Marks as synced after a successful update.
     *
     * @param mode The mode to update.
     */
    suspend fun updateMode(mode: Mode) {
        val localMode = ModeMapper.toEntity(mode).copy(isSynced = false)
        modeDao.updateMode(localMode)

        try {
            val backendId = mode.id ?: localMode.backendId
            if (backendId != null) {
                modeApiService.updateMode(backendId, ModeMapper.toDto(mode))
                modeDao.updateMode(localMode.copy(isSynced = true))
            } else {
                Log.e("ModeRepository", "Cannot update mode, no backend ID assigned")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Marks a mode as deleted and schedules deletion.
     *
     * - Updates the local record as deleted.
     * - Schedules a WorkManager job to sync the deletion.
     *
     * @param mode The mode to delete.
     */

    suspend fun deleteMode(mode: Mode) {
        val localMode = mode.copy(isDeleted = true, isSynced = false)
        modeDao.updateMode(ModeMapper.toEntity(localMode))
        scheduleModeDeletion()
    }


    /**
     * Syncs modes with the backend.
     *
     * - Ensures modes are fetched from the backend only once.
     * - Schedules background sync jobs when needed.
     */
    suspend fun syncModes() {
        if (!syncStatusManager.hasSyncedModes()) {
            fetchModesFromBackendAndCache()
            syncStatusManager.setModesSynced(true)
        } else {
            scheduleModeSync()
        }
    }

    /**
     * Fetches modes from the backend and caches new ones locally.
     */
    override suspend fun fetchModesFromBackendAndCache() {
        try {
            val remoteModes = modeApiService.getModes()

            val existingModes = modeDao.getAllModesOnce().map { it.backendId }

            val newModes = remoteModes
                .map { ModeMapper.fromDto(it) }
                .filter { it.id !in existingModes }

            if (newModes.isNotEmpty()) {
                modeDao.insertModes(newModes.map { ModeMapper.toEntity(it) })
            } else {
                Log.e("ModeRepository", "No new modes to insert")
            }
        } catch (e: Exception) {
            Log.e("ModeRepository", "Error fetching modes: ${e.message}")
        }
    }

    /**
     * Schedules a background sync job for modes.
     */
    private fun scheduleModeSync() {
        val workRequest = OneTimeWorkRequestBuilder<SyncModeWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        workManager.enqueueUniqueWork("SYNC_MODE_WORK", ExistingWorkPolicy.KEEP, workRequest)
    }

    /**
     * Schedules a background deletion job for modes.
     */
    private fun scheduleModeDeletion() {
        val workRequest = OneTimeWorkRequestBuilder<DeleteModeWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()

        workManager.enqueueUniqueWork("DELETE_MODE_WORK", ExistingWorkPolicy.KEEP, workRequest)
    }

    /**
     * Syncs modes marked as deleted with the backend.
     *
     * - Deletes modes from the backend.
     * - Removes them from the local database if successful.
     */
    suspend fun syncDeletedModes() {
        val deletedModes = modeDao.getDeletedModes()

        for (mode in deletedModes) {
            try {
                mode.backendId?.let { modeApiService.deleteMode(it) }
                modeDao.deleteMode(mode.localId)
            } catch (e: Exception) {
                Log.e("ModeRepository", "Failed to delete mode from backend: ${mode.backendId}")
            }
        }
    }
}
