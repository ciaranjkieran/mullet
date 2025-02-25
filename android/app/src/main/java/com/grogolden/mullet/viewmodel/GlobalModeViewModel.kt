package com.grogolden.mullet.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

import javax.inject.Inject

import com.grogolden.mullet.data.models.Mode
import com.grogolden.mullet.data.repository.ModeRepository

/**
 * ViewModel for managing modes across the app.
 *
 * - Fetches and manages available modes.
 * - Handles selecting, adding, updating, and deleting modes.
 * - Syncs modes with a remote database using WorkManager.
 *
 * @param modeRepository Repository handling data operations for modes.
 * @param workManager WorkManager instance for background sync operations.
 */
@HiltViewModel
class GlobalModeViewModel @Inject constructor(
    private val modeRepository: ModeRepository,
    private val workManager: WorkManager,
) : ViewModel() {

    // Holds the currently selected mode
    private val _selectedMode = MutableStateFlow<Mode?>(null)
    val selectedMode: StateFlow<Mode?> = _selectedMode

    // Holds the list of available modes
    private val _modes = MutableStateFlow<List<Mode>>(emptyList())
    val modes: StateFlow<List<Mode>> = _modes

    // Tracks whether a background sync is in progress
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing

    init {
        fetchModes()
        observeSyncWorker() // Observe the sync worker status
    }

    /**
     * Fetches modes from the repository.
     *
     * - Triggers a sync with the remote database.
     * - Updates the local list of modes.
     * - Selects the first available mode if none is selected.
     */
    private fun fetchModes() {
        viewModelScope.launch {
            // Start sync process
            modeRepository.syncModes()

            // Collect and update local mode list
            modeRepository.getAllModes().collect { fetchedModes ->
                _modes.value = fetchedModes
                _selectedMode.update { current -> current ?: fetchedModes.firstOrNull() }
            }

        }
    }

    /**
     * Updates the currently selected mode.
     *
     * @param mode The mode to be set as active.
     */
    fun selectMode(mode: Mode) {
        _selectedMode.value = mode
    }

    /**
     * Adds a new mode to the database.
     *
     * @param mode The new mode to insert.
     * @param onInserted Callback function executed with the inserted mode.
     */
    fun addMode(mode: Mode, onInserted: (Mode) -> Unit) {
        viewModelScope.launch {
            val insertedMode = modeRepository.addMode(mode)
            onInserted(insertedMode) // Callback with the inserted mode
        }
    }

    /**
     * Updates an existing mode in the database.
     *
     * @param mode The updated mode.
     */
    fun updateMode(mode: Mode) {
        viewModelScope.launch {
            modeRepository.updateMode(mode)
        }
    }

    /**
     * Deletes a mode from the database.
     *
     * - If the deleted mode is currently selected, it selects the first available mode.
     *
     * @param mode The mode to delete.
     */
    fun deleteMode(mode: Mode) {
        viewModelScope.launch {
            modeRepository.deleteMode(mode)
            _selectedMode.update { current ->
                if (current == mode) _modes.value.firstOrNull() else current
            }
        }
    }

    /**
     * Observes the status of the WorkManager sync job.
     *
     * Updates `_isSyncing` to `true` if the sync job is running.
     */
    private fun observeSyncWorker() {
        workManager.getWorkInfosForUniqueWorkLiveData("MODE_SYNC_WORK")
            .observeForever { workInfos ->
                _isSyncing.value = workInfos.any { it.state == WorkInfo.State.RUNNING }
            }
    }
}
