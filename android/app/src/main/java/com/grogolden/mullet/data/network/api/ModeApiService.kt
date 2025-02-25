package com.grogolden.mullet.data.network.api

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

import com.grogolden.mullet.data.network.dto.ModeDto

/**
 * Retrofit API service for managing modes.
 *
 * - Provides network operations for retrieving, creating, updating, and deleting modes.
 * - Uses `ModeDto` for data transfer between the backend and the app.
 */
interface ModeApiService {

    /**
     * Retrieves a list of all modes from the backend.
     *
     * @return A list of `ModeDto` representing all available modes.
     */
    @GET("api/modes/")
    suspend fun getModes(): List<ModeDto>

    /**
     * Creates a new mode on the backend.
     *
     * @param mode The `ModeDto` containing mode details.
     * @return The created mode as returned by the backend.
     */
    @POST("api/modes/")
    suspend fun createMode(@Body mode: ModeDto): ModeDto

    /**
     * Updates an existing mode on the backend.
     *
     * @param id The unique ID of the mode to update.
     * @param mode The updated `ModeDto` containing new mode details.
     * @return The updated mode as returned by the backend.
     */
    @PUT("api/modes/{id}/")
    suspend fun updateMode(@Path("id") id: Int, @Body mode: ModeDto): ModeDto

    /**
     * Deletes a mode from the backend.
     *
     * @param id The unique ID of the mode to delete.
     */
    @DELETE("api/modes/{id}/")
    suspend fun deleteMode(@Path("id") id: Int)
}
