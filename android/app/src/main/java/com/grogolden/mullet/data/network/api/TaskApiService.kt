package com.grogolden.mullet.data.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import com.grogolden.mullet.data.network.dto.TaskDto

/**
 * Retrofit API service for managing tasks.
 *
 * - Provides network operations for retrieving, creating, updating, and deleting tasks.
 * - Uses `TaskDto` for data transfer between the backend and the app.
 */
interface TaskApiService {

    /**
     * Retrieves a list of all tasks from the backend.
     *
     * @return A list of `TaskDto` representing all available tasks.
     */    @GET("api/tasks/")
    suspend fun getTasks(): List<TaskDto>

    /**
     * Creates a new task on the backend.
     *
     * @param task The `TaskDto` containing task details.
     * @return The created task as returned by the backend.
     */    @POST("api/tasks/")
    suspend fun createTask(@Body task: TaskDto): TaskDto

    /**
     * Updates an existing task on the backend.
     *
     * @param id The unique ID of the task to update.
     * @param task The updated `TaskDto` containing new task details.
     * @return The updated task as returned by the backend.
     */
    @PUT("api/tasks/{id}/")
    suspend fun updateTask(@Path("id") id: Int, @Body task: TaskDto): TaskDto

    /**
     * Deletes a task from the backend.
     *
     * @param id The unique ID of the task to delete.
     */
    @DELETE("api/tasks/{id}/")
    suspend fun deleteTask(@Path("id") id: Int)
}
