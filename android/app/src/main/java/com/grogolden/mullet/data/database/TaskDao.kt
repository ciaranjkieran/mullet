package com.grogolden.mullet.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

import com.grogolden.mullet.data.local.entities.TaskEntity

/**
 * DAO (Data Access Object) for managing task-related database operations.
 *
 * - Provides methods for CRUD operations on the `tasks` table.
 * - Supports Flow for real-time task observation.
 */
@Dao
interface TaskDao {

    /**
     * Retrieves all active tasks (excluding soft-deleted ones).
     *
     * @return A Flow that emits updates whenever tasks change.
     */
    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    fun getAllTasks(): Flow<List<TaskEntity>>

    /**
     * Retrieves all active tasks once (without observing future changes).
     *
     * @return A list of tasks that are not marked as deleted.
     */
    @Query("SELECT * FROM tasks WHERE isDeleted = 0")
    suspend fun getAllTasksOnce(): List<TaskEntity>

    /**
     * Fetches a task by its local ID.
     *
     * @param localId The unique ID of the task.
     * @return The corresponding TaskEntity, if found.
     */
    @Query("SELECT * FROM tasks WHERE localId = :localId LIMIT 1")
    suspend fun getTaskByLocalId(localId: Long): TaskEntity

    /**
     * Inserts a new task into the database, replacing any existing entry if a conflict occurs.
     *
     * @param task The task to insert.
     * @return The newly inserted task's ID.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    /**
     * Inserts multiple tasks into the database, replacing existing entries if conflicts occur.
     *
     * @param tasks The list of tasks to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    /**
     * Updates an existing task in the database.
     *
     * @param task The task with updated values.
     */
    @Update
    suspend fun updateTask(task: TaskEntity)

    /**
     * Retrieves a list of tasks that are marked as deleted but not yet synced.
     *
     * @return A list of soft-deleted tasks awaiting synchronization.
     */
    @Query("SELECT * FROM tasks WHERE isDeleted = 1 AND isSynced = 0")
    suspend fun getDeletedTasks(): List<TaskEntity>

    @Query("UPDATE tasks SET isDeleted = 1 WHERE localId = :localId")
    suspend fun markTaskAsDeleted(localId: Long)

    /**
     * Permanently deletes all tasks that have been marked as deleted.
     */
    @Query("DELETE FROM tasks WHERE isDeleted = 1")
    suspend fun deleteMarkedTasks()
}
