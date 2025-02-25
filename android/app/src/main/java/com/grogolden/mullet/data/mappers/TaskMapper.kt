package com.grogolden.mullet.data.mappers

import com.grogolden.mullet.data.local.entities.ModeEntity
import com.grogolden.mullet.data.local.entities.TaskEntity

import com.grogolden.mullet.data.models.Task

import com.grogolden.mullet.data.network.dto.TaskDto

/**
 * Mapper object for converting between different representations of `Task`.
 *
 * - Handles conversions between Room database entities, domain models, and DTOs.
 * - Ensures consistency when interacting with different data layers.
 */
object TaskMapper {

    /**
     * Converts a Room `TaskEntity` to a domain `Task` model.
     *
     * @param entity The database entity to convert.
     * @return The corresponding domain model.
     */
    fun entityToTask(entity: TaskEntity): Task =
        Task(
            localId = entity.localId,
            id = entity.backendId,
            title = entity.title,
            modeId = entity.modeId,
            isCompleted = entity.isCompleted,
            timeLogged = entity.timeLogged,
            isSynced = entity.isSynced,
            isDeleted = entity.isDeleted // Track deletion for sync
        )

    /**
     * Converts a domain `Task` model to a Room `TaskEntity` for database storage.
     *
     * @param task The domain model to convert.
     * @return The corresponding Room entity.
     */
    fun taskToEntity(task: Task): TaskEntity =
        TaskEntity(
            localId = task.localId,
            backendId = task.id,
            title = task.title,
            modeId = task.modeId,
            isCompleted = task.isCompleted,
            timeLogged = task.timeLogged,
            isSynced = task.isSynced,
            isDeleted = task.isDeleted
        )

    /**
     * Converts a backend `TaskDto` to a domain `Task` model.
     *
     * - Finds the corresponding local mode by matching its backend ID.
     * - If the mode is not found, assigns `modeId = -1L` as a fallback.
     *
     * @param dto The DTO from the API response.
     * @param modeEntities The list of local ModeEntities to match with the task.
     * @return The corresponding domain model.
     */
    fun fromDto(dto: TaskDto, modeEntities: List<ModeEntity>): Task {
        val matchingMode = modeEntities.find { it.backendId == dto.modeId }
        return Task(
            id = dto.id,
            title = dto.title,
            modeId = matchingMode?.localId ?: -1L,
            isCompleted = dto.isCompleted,
            timeLogged = dto.timeLogged,
            isSynced = true,
            isDeleted = dto.isDeleted
        )
    }

    /**
     * Converts a domain `Task` model to a `TaskDto` for API requests.
     *
     * - Finds the corresponding backend mode ID from the local mode list.
     * - Ensures `modeId` is always a backend ID before sending.
     *
     * @param task The domain model to convert.
     * @param modeEntities The list of local ModeEntities to match with the task.
     * @return The corresponding DTO for network communication.
     */
    fun toDto(task: Task, modeEntities: List<ModeEntity>): TaskDto {
        val backendModeId = modeEntities.find { it.localId == task.modeId }?.backendId

        return TaskDto(
            id = task.id ?: 0,
            title = task.title,
            modeId = backendModeId ?: 0,
            isCompleted = task.isCompleted,
            timeLogged = task.timeLogged,
            isDeleted = task.isDeleted
        )
    }
}
