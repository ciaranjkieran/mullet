package com.grogolden.mullet.data.models

/**
 * Domain model representing a mode.
 *
 * - Used in the business logic layer of the app.
 * - Serves as an abstraction between the database, UI, and API layers.
 */
data class Mode(

    /** Local Room database ID (used for internal tracking). */
    val localId: Long = 0,

    /** Backend-assigned ID (null until synced). */
    val id: Int? = null,

    /** The title of the mode (e.g., "Work", "Play"). */
    val title: String,

    /** Color representation in hex format (e.g., "#FFD700"). */
    val color: String,

    /** Indicates whether this mode has been synced with the backend. */
    val isSynced: Boolean = false,

    /** Marks if the mode is scheduled for deletion. */
    val isDeleted: Boolean = false,
)
