package com.grogolden.mullet.data.models

/**
 * Domain model representing a task.
 *
 * - Used as an abstraction between the database, UI, and API layers.
 * - Ensures consistency across different parts of the application.
 */
data class Task(

    /** Local Room database ID (used for internal tracking). */
    val localId: Long = 0,

    /** Backend-assigned ID (null until synced). */
    val id: Int? = null,

    /** The title of the task. */
    val title: String,

    /** Foreign key reference to `Mode` (links task to a mode). */
    val modeId: Long,

    /** Indicates whether the task has been completed. */
    val isCompleted: Boolean = false,

    /** Time logged on the task (in seconds). */
    val timeLogged: Long = 0L,

    /** Indicates whether this task has been synced with the backend. */
    val isSynced: Boolean = false,

    /** Marks if the task is scheduled for deletion. */
    val isDeleted: Boolean = false,
)
