package com.grogolden.mullet.data.mappers

import com.grogolden.mullet.data.local.entities.ModeEntity

import com.grogolden.mullet.data.models.Mode

import com.grogolden.mullet.data.network.dto.ModeDto

/**
 * Mapper object for converting between different representations of `Mode`.
 *
 * - Handles conversions between Room database entities, domain models, and DTOs.
 * - Ensures consistency when interacting with different data layers.
 */
object ModeMapper {

    /**
     * Converts a Room `ModeEntity` to a domain `Mode` model.
     *
     * @param entity The database entity to convert.
     * @return The corresponding domain model.
     */
    fun entityToMode(entity: ModeEntity): Mode =
        Mode(
            localId = entity.localId,
            id = entity.backendId,
            title = entity.title,
            color = entity.color,
            isSynced = entity.isSynced,
            isDeleted = entity.isDeleted // Track deletion for sync
        )

    /**
     * Converts a domain `Mode` model to a Room `ModeEntity` for database storage.
     *
     * @param mode The domain model to convert.
     * @return The corresponding Room entity.
     */
    fun toEntity(mode: Mode): ModeEntity =
        ModeEntity(
            localId = mode.localId,
            backendId = mode.id,
            title = mode.title,
            color = mode.color,
            isSynced = mode.isSynced,
            isDeleted = mode.isDeleted
        )

    /**
     * Converts a backend `ModeDto` to a domain `Mode` model.
     *
     * - Assigns `localId = 0` since Room will generate it on insert.
     * - Marks the mode as synced since it originates from the backend.
     *
     * @param dto The DTO from the API response.
     * @return The corresponding domain model.
     */
    fun fromDto(dto: ModeDto): Mode =
        Mode(
            localId = 0,  // Room will assign this on insert
            id = dto.id,
            title = dto.title,
            color = dto.color,
            isSynced = true,
            isDeleted = dto.isDeleted // Track deletion
        )

    /**
     * Converts a domain `Mode` model to a `ModeDto` for API requests.
     *
     * - Ensures the `id` is non-null before sending it to the backend.
     *
     * @param mode The domain model to convert.
     * @return The corresponding DTO for network communication.
     */
    fun toDto(mode: Mode): ModeDto =
        ModeDto(
            id = mode.id ?: 0,
            title = mode.title,
            color = mode.color,
            isDeleted = mode.isDeleted
        )
}
