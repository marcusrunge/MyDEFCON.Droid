package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.os.Message
import androidx.lifecycle.viewModelScope
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the status screen.
 *
 * This ViewModel is responsible for managing the DEFCON status. It reflects the current
 * status on the UI and allows the user to select a new status. The status is persisted
 * and distributed to other parts of the application.
 *
 * @param app The application instance.
 * @param core The core component for accessing shared preferences and live data.
 */
@HiltViewModel
class StatusViewModel @Inject constructor(
    private val app: Application,
    private val core: Core
) : ObservableViewModel(app) {

    private val _isDefcon1ButtonChecked = MutableStateFlow(false)
    /** A state flow representing whether the DEFCON 1 button is checked. */
    val isDefcon1ButtonChecked = _isDefcon1ButtonChecked.asStateFlow()

    private val _isDefcon2ButtonChecked = MutableStateFlow(false)
    /** A state flow representing whether the DEFCON 2 button is checked. */
    val isDefcon2ButtonChecked = _isDefcon2ButtonChecked.asStateFlow()

    private val _isDefcon3ButtonChecked = MutableStateFlow(false)
    /** A state flow representing whether the DEFCON 3 button is checked. */
    val isDefcon3ButtonChecked = _isDefcon3ButtonChecked.asStateFlow()

    private val _isDefcon4ButtonChecked = MutableStateFlow(false)
    /** A state flow representing whether the DEFCON 4 button is checked. */
    val isDefcon4ButtonChecked = _isDefcon4ButtonChecked.asStateFlow()

    private val _isDefcon5ButtonChecked = MutableStateFlow(false)
    /** A state flow representing whether the DEFCON 5 button is checked. */
    val isDefcon5ButtonChecked = _isDefcon5ButtonChecked.asStateFlow()

    init {
        // Initialize button states from persisted preferences.
        setDefconStatusButton(core.preferences?.status ?: 5)

        // Observe changes to the DEFCON status from other sources.
        viewModelScope.launch {
            core.liveDataManager?.defconStatusFlow?.collect { (status, source) ->
                // Avoid updating if the change originated from this ViewModel.
                if (source != StatusViewModel::class.java) {
                    setDefconStatusButton(status)
                }
            }
        }
    }

    /**
     * Updates the UI based on messages from background threads.
     * @param inputMessage The message containing the new DEFCON status.
     */
    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) {
            setDefconStatusButton(inputMessage.obj as Int)
        }
    }

    /**
     * Sets the checked state of the DEFCON status buttons based on the given status.
     * @param status The current DEFCON status (1-5).
     */
    private fun setDefconStatusButton(status: Int) {
        _isDefcon1ButtonChecked.value = status == 1
        _isDefcon2ButtonChecked.value = status == 2
        _isDefcon3ButtonChecked.value = status == 3
        _isDefcon4ButtonChecked.value = status == 4
        // Default to DEFCON 5 if status is invalid.
        _isDefcon5ButtonChecked.value = status == 5 || status !in 1..4
    }

    /**
     * Handles the user selecting a new DEFCON status from the UI.
     *
     * @param status The newly selected DEFCON status.
     */
    fun onDefconStatusSelected(status: Int) {
        // Do nothing if the status has not changed.
        if (core.preferences?.status == status) {
            return
        }
        // Update the UI immediately for a responsive feel.
        setDefconStatusButton(status)
        // Persist and distribute the new status.
        distributeDefconStatus(status)
    }

    /**
     * Persists the new DEFCON status in shared preferences and broadcasts it to other parts of the app.
     *
     * @param status The new DEFCON status.
     */
    private fun distributeDefconStatus(status: Int) {
        core.preferences?.status = status
        core.liveDataManager?.emitDefconStatus(status, StatusViewModel::class.java)
    }
}
