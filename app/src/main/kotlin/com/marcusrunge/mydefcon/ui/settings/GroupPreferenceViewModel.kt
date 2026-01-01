package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.installations.FirebaseInstallations
import com.marcusrunge.mydefcon.BR
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * ViewModel for the group preference screen.
 *
 * This ViewModel handles the logic for creating, deleting, joining, and leaving DEFCON groups.
 * It interacts with Firebase for group management and updates the UI state accordingly.
 *
 * @param app The application instance.
 * @param core The core component for accessing shared preferences.
 * @param firebase The Firebase component for database interactions.
 */
@HiltViewModel
class GroupPreferenceViewModel @Inject constructor(
    app: Application, private val core: Core, private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {

    private val _scanQrCodeEvent = MutableLiveData<Boolean>()
    val scanQrCodeEvent: LiveData<Boolean> = _scanQrCodeEvent

    private var _textToEncode: String = ""
    private var _isCreateGroupButtonEnabled: Boolean = false
    private var _isDeleteGroupButtonEnabled: Boolean = false
    private var _isJoinGroupButtonEnabled: Boolean = false
    private var _isLeaveGroupButtonEnabled: Boolean = false

    /**
     * The text to be encoded in the QR code, typically the created group ID.
     */
    @get:Bindable
    var textToEncode: String
        get() = _textToEncode
        set(value) {
            _textToEncode = value
            notifyPropertyChanged(BR.textToEncode)
        }

    /**
     * A boolean indicating whether the 'Create Group' button is enabled.
     */
    @get:Bindable
    var isCreateGroupButtonEnabled: Boolean
        get() = _isCreateGroupButtonEnabled
        set(value) {
            _isCreateGroupButtonEnabled = value
            notifyPropertyChanged(BR.createGroupButtonEnabled)
        }

    /**
     * A boolean indicating whether the 'Delete Group' button is enabled.
     */
    @get:Bindable
    var isDeleteGroupButtonEnabled: Boolean
        get() = _isDeleteGroupButtonEnabled
        set(value) {
            _isDeleteGroupButtonEnabled = value
            notifyPropertyChanged(BR.deleteGroupButtonEnabled)
        }

    /**
     * A boolean indicating whether the 'Join Group' button is enabled.
     */
    @get:Bindable
    var isJoinGroupButtonEnabled: Boolean
        get() = _isJoinGroupButtonEnabled
        set(value) {
            _isJoinGroupButtonEnabled = value
            notifyPropertyChanged(BR.joinGroupButtonEnabled)
        }

    /**
     * A boolean indicating whether the 'Leave Group' button is enabled.
     */
    @get:Bindable
    var isLeaveGroupButtonEnabled: Boolean
        get() = _isLeaveGroupButtonEnabled
        set(value) {
            _isLeaveGroupButtonEnabled = value
            notifyPropertyChanged(BR.leaveGroupButtonEnabled)
        }

    init {
        updateButtonStates()
    }

    /**
     * Handles the click event for the 'Create Group' button.
     * Creates a new DEFCON group in Firebase and updates the UI.
     */
    fun onCreateGroupClicked() {
        viewModelScope.launch {
            try {
                val defconGroupId = withContext(Dispatchers.IO) {
                    firebase.firestore.createDefconGroup()
                }
                if (defconGroupId.isNotEmpty()) {
                    core.preferences?.createdDefconGroupId = defconGroupId
                    textToEncode = defconGroupId
                    updateButtonStates()
                    Log.d("GroupPreferenceViewModel", "DEFCON Group ID created: $defconGroupId")
                } else {
                    Log.w("GroupPreferenceViewModel", "DEFCON Group ID is empty")
                }
            } catch (e: Exception) {
                Log.e("GroupPreferenceViewModel", "Error during group creation", e)
            }
        }
    }

    /**
     * Handles the click event for the 'Delete Group' button.
     * Deletes the created DEFCON group from Firebase and updates the UI.
     */
    fun onDeleteGroupClicked() {
        viewModelScope.launch {
            try {
                core.preferences?.createdDefconGroupId?.takeIf { it.isNotEmpty() }?.let {
                    firebase.firestore.deleteDefconGroup(it)
                    core.preferences?.createdDefconGroupId = ""
                    updateButtonStates()
                }
            } catch (e: Exception) {
                Log.e("GroupPreferenceViewModel", "Error during group deletion", e)
            }
        }
    }

    /**
     * Handles the click event for the 'Join Group' button.
     * Triggers the QR code scanner to join a group.
     */
    fun onJoinGroupClicked() {
        _scanQrCodeEvent.value = true
    }

    /**
     * Handles the click event for the 'Leave Group' button.
     * Clears the joined group ID from preferences and updates the UI.
     */
    fun onLeaveGroupClicked() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val leftGroupId = core.preferences?.joinedDefconGroupId
                    core.preferences?.joinedDefconGroupId = ""
                    updateButtonStates()
                    Log.d("GroupPreferenceViewModel", "DEFCON Group ID left: $leftGroupId")
                }
            } catch (e: Exception) {
                Log.e("GroupPreferenceViewModel", "Error leaving group", e)
            }
        }
    }

    /**
     * Processes the result from the QR code scanner.
     * If a valid group ID is scanned, it joins the group.
     * @param groupIdToJoin The scanned group ID, or null if cancelled.
     */
    fun processQrCodeResult(groupIdToJoin: String?) {
        _scanQrCodeEvent.value = false // Reset the event
        if (groupIdToJoin.isNullOrEmpty()) {
            Log.w("GroupPreferenceViewModel", "QR code result was empty or null")
            return
        }

        viewModelScope.launch {
            try {
                val installationId = FirebaseInstallations.getInstance().id.await()
                withContext(Dispatchers.IO) {
                    firebase.firestore.joinDefconGroup(groupIdToJoin, installationId)
                }
                core.preferences?.joinedDefconGroupId = groupIdToJoin
                updateButtonStates()
                Log.d("GroupPreferenceViewModel", "Successfully joined group: $groupIdToJoin")
            } catch (e: Exception) {
                Log.e("GroupPreferenceViewModel", "Error joining group: $groupIdToJoin", e)
            }
        }
    }

    /**
     * Updates the UI based on messages from background threads. (Not yet implemented)
     * @param inputMessage The message containing update information.
     */
    override fun updateView(inputMessage: Message) {
        // TODO: Not yet implemented
    }

    /**
     * Updates the enabled state of the group management buttons based on the current
     * created and joined group IDs stored in shared preferences.
     */
    private fun updateButtonStates() {
        val createdGroupId = core.preferences?.createdDefconGroupId ?: ""
        val joinedGroupId = core.preferences?.joinedDefconGroupId ?: ""

        textToEncode = when {
            createdGroupId.isNotEmpty() -> createdGroupId
            joinedGroupId.isNotEmpty() -> joinedGroupId
            else -> ""
        }

        isCreateGroupButtonEnabled = createdGroupId.isEmpty() && joinedGroupId.isEmpty()
        isDeleteGroupButtonEnabled = createdGroupId.isNotEmpty()
        isJoinGroupButtonEnabled = createdGroupId.isEmpty() && joinedGroupId.isEmpty()
        isLeaveGroupButtonEnabled = joinedGroupId.isNotEmpty()
    }
}
