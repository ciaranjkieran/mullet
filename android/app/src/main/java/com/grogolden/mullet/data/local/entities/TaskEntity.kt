package com.grogolden.mullet.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room database entity representing a task.
 *
 * - Stored in the `tasks` table.
 * - Uses a foreign key reference to `ModeEntity` for categorization.
 * - Supports synchronization with a backend service.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = ModeEntity::class,
            parentColumns = ["localId"],
            childColumns = ["modeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["modeId"])]
)
data class TaskEntity(

    /** Auto-generated primary key for local database identification. */
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,

    /** Backend-assigned ID, null until synced. */
    val backendId: Int? = null,

    /** The title of the task. */
    val title: String,

    /** Foreign key reference to `ModeEntity` for task categorization. */
    val modeId: Long,

    /** Indicates whether the task has been completed. */
    val isCompleted: Boolean = false,

    /** Time logged on the task (in seconds). */
    val timeLogged: Long = 0L,

    /** Indicates whether the task has been synced with the backend. */
    val isSynced: Boolean = false,

    /** Marks if the task is scheduled for deletion. */
    val isDeleted: Boolean = false,
)
