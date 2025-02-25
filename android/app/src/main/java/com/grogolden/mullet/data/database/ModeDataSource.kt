package com.grogolden.mullet.data.database

import com.grogolden.mullet.data.local.entities.ModeEntity

/**
 * Data source interface for handling mode-related operations.
 *
 * - Abstracts data retrieval from different sources (local database, network, etc.).
 * - Allows the repository to depend on this interface instead of a specific implementation.
 */
interface ModeDataSource {

    /**
     * Retrieves all modes from the local database (without real-time observation).
     *
     * @return A list of all stored modes.
     */
    suspend fun getAllModesOnce(): List<ModeEntity>

    /**
     * Fetches modes from the backend API and caches them in the local database.
     */
    suspend fun fetchModesFromBackendAndCache()
}

