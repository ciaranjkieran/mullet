package com.grogolden.mullet.data.network.dto

/**
 * Data Transfer Object (DTO) representing a task in API requests and responses.
 *
 * - Used to transfer task data between the app and the backend.
 * - Maps directly to the API's task representation.
 */
data class TaskDto(

    /** Unique identifier assigned by the backend. */
    val id: Int,

    /** The title of the task. */
    val title: String,

    /** The backend-assigned mode ID that this task belongs to. */
    val modeId: Int,

    /** Indicates whether the task has been completed. */
    val isCompleted: Boolean,

    /** Time logged on the task (in seconds). */
    val timeLogged: Long,

    /** Indicates whether the task has been deleted on the backend. */
    val isDeleted: Boolean,
)
