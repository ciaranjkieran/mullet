package com.grogolden.mullet.data.network.dto

/**
 * Data Transfer Object (DTO) representing a mode in API requests and responses.
 *
 * - Used to transfer mode data between the app and the backend.
 * - Maps directly to the API's mode representation.
 */
data class ModeDto(

    /** Unique identifier assigned by the backend. */
    val id: Int,

    /** The title of the mode (e.g., "Work", "Play"). */
    val title: String,

    /** Color representation in hex format (e.g., "#FFD700"). */
    val color: String,

    /** Indicates whether the mode has been deleted on the backend. */
    val isDeleted: Boolean = false,
)
