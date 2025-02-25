package com.grogolden.mullet.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room database entity representing a mode.
 *
 * - Stored in the `modes` table.
 * - Supports synchronization with a backend service.
 * - Uses a local auto-generated ID and an optional backend ID.
 */
@Entity(tableName = "modes")
data class ModeEntity(

    /** Auto-generated primary key for local database identification. */
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,

    /** Backend-assigned ID, null until synced. */
    val backendId: Int? = null,

    /** The title of the mode (e.g., "Work", "Play"). */
    val title: String,

    /** Colour representation in hex format (e.g., "#FF5733"). */
    val color: String,

    /** Indicates whether the mode has been synced with the backend. */
    val isSynced: Boolean = false,

    /** Marks if the mode is scheduled for deletion. */
    val isDeleted: Boolean = false,
)
