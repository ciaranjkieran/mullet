package com.grogolden.mullet.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

import com.grogolden.mullet.data.local.entities.ModeEntity

/**
 * Data Access Object (DAO) for managing mode-related database operations.
 *
 * - Provides methods for CRUD operations on the `modes` table.
 * - Supports flow-based observation for real-time updates.
 */
@Dao
interface ModeDao {

    /**
     * Retrieves all active modes (excluding deleted ones).
     *
     * @return A Flow that emits updates whenever modes change.
     */
    @Query("SELECT * FROM modes WHERE isDeleted = 0")
    fun getAllModes(): Flow<List<ModeEntity>>

    /**
     * Retrieves all active modes once (without observing future changes).
     *
     * @return A list of modes that are not marked as deleted.
     */
    @Query("SELECT * FROM modes WHERE isDeleted = 0")
    suspend fun getAllModesOnce(): List<ModeEntity>

    /**
     * Fetches a mode by its local ID.
     *
     * @param localId The unique ID of the mode.
     * @return The corresponding ModeEntity, if found.
     */
    @Query("SELECT * FROM modes WHERE localId = :localId LIMIT 1")
    suspend fun getModeByLocalId(localId: Long): ModeEntity

    /**
     * Inserts a mode into the database, replacing any existing entry if a conflict occurs.
     *
     * @param mode The mode to insert.
     * @return The newly inserted mode's ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMode(mode: ModeEntity): Long

    /**
     * Inserts multiple modes into the database, replacing any existing entries if conflicts occur.
     *
     * @param modes The list of modes to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModes(modes: List<ModeEntity>)

    /**
     * Updates an existing mode in the database.
     *
     * @param mode The mode with updated values.
     */
    @Update
    suspend fun updateMode(mode: ModeEntity)

    /**
     * Deletes a mode by its local ID.
     *
     * @param modeId The ID of the mode to delete.
     */
    @Query("DELETE FROM modes WHERE localId = :modeId")
    suspend fun deleteMode(modeId: Long) // ✅ Deletes mode by localId

    /**
     * Retrieves a list of modes that are marked as deleted but not yet synced.
     *
     * @return A list of soft-deleted modes awaiting synchronization.
     */
    @Query("SELECT * FROM modes WHERE isDeleted = 1 AND isSynced = 0")
    suspend fun getDeletedModes(): List<ModeEntity>

    /**
     * Permanently deletes all modes that are marked as deleted.
     */
    @Query("DELETE FROM modes WHERE isDeleted = 1")
    suspend fun deleteMarkedModes()
}
