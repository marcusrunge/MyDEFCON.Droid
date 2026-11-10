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
 * This ViewModel handles the logic for creating, deleting, joining and leaving
 * DEFCON groups. It interacts with Firebase for group management and updates
 * the UI state accordingly.
 *
 * @param app The application instance.
 * @param core The core component for accessing shared preferences.
 * @param firebase The Firebase component for database interactions.
 */
@HiltViewModel
class GroupPreferenceViewModel @Inject constructor(
    app: Application,
    private val core: Core,
    private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {

    private val _scanQrCodeEvent = MutableLiveData(false)

    /**
     * Indicates that the settings Fragment should launch the QR code scanner.
     *
     * The Fragment must mark the request as handled before launching the scanner
     * to prevent recreation of the Fragment from launching it a second time.
     */
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
     * Indicates whether the Create Group button is enabled.
     */
    @get:Bindable
    var isCreateGroupButtonEnabled: Boolean
        get() = _isCreateGroupButtonEnabled
        set(value) {
            _isCreateGroupButtonEnabled = value
            notifyPropertyChanged(BR.createGroupButtonEnabled)
        }

    /**
     * Indicates whether the Delete Group button is enabled.
     */
    @get:Bindable
    var isDeleteGroupButtonEnabled: Boolean
        get() = _isDeleteGroupButtonEnabled
        set(value) {
            _isDeleteGroupButtonEnabled = value
            notifyPropertyChanged(BR.deleteGroupButtonEnabled)
        }

    /**
     * Indicates whether the Join Group button is enabled.
     */
    @get:Bindable
    var isJoinGroupButtonEnabled: Boolean
        get() = _isJoinGroupButtonEnabled
        set(value) {
            _isJoinGroupButtonEnabled = value
            notifyPropertyChanged(BR.joinGroupButtonEnabled)
        }

    /**
     * Indicates whether the Leave Group button is enabled.
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
     * Creates a new DEFCON group and stores its identifier.
     */
    fun onCreateGroupClicked() {
        viewModelScope.launch {
            try {
                val defconGroupId = withContext(Dispatchers.IO) {
                    firebase.firestore.createDefconGroup()
                }

                if (defconGroupId.isEmpty()) {
                    Log.w(TAG, "DEFCON Group ID is empty")
                    return@launch
                }

                core.preferences?.createdDefconGroupId = defconGroupId
                core.liveDataManager?.emitDefconGroupChange(
                    defconGroupId,
                    "",
                    GroupPreferenceViewModel::class.java
                )

                textToEncode = defconGroupId
                updateButtonStates()

                Log.d(TAG, "DEFCON Group ID created: $defconGroupId")
            } catch (exception: Exception) {
                Log.e(TAG, "Error during group creation", exception)
            }
        }
    }

    /**
     * Deletes the DEFCON group created by this application installation.
     */
    fun onDeleteGroupClicked() {
        viewModelScope.launch {
            try {
                val createdGroupId = core.preferences?.createdDefconGroupId
                    ?.takeIf { it.isNotEmpty() }
                    ?: return@launch

                withContext(Dispatchers.IO) {
                    firebase.firestore.deleteDefconGroup(createdGroupId)
                }

                core.preferences?.createdDefconGroupId = ""
                core.liveDataManager?.emitDefconGroupChange(
                    "",
                    "",
                    GroupPreferenceViewModel::class.java
                )

                updateButtonStates()

                Log.d(TAG, "DEFCON Group ID deleted: $createdGroupId")
            } catch (exception: Exception) {
                Log.e(TAG, "Error during group deletion", exception)
            }
        }
    }

    /**
     * Requests that the settings Fragment launch the QR code scanner.
     */
    fun onJoinGroupClicked() {
        _scanQrCodeEvent.value = true
    }

    /**
     * Marks the scanner launch request as handled.
     *
     * This method must be called before the scanner Activity is launched. It
     * prevents a Fragment recreation from launching another scanner instance.
     */
    fun onQrCodeScannerLaunchHandled() {
        _scanQrCodeEvent.value = false
    }

    /**
     * Leaves the currently joined DEFCON group.
     */
    fun onLeaveGroupClicked() {
        viewModelScope.launch {
            try {
                val leftGroupId = withContext(Dispatchers.IO) {
                    val groupId = core.preferences?.joinedDefconGroupId

                    core.preferences?.joinedDefconGroupId = ""
                    core.liveDataManager?.emitDefconGroupChange(
                        "",
                        "",
                        GroupPreferenceViewModel::class.java
                    )

                    groupId
                }

                /*
                 * Bindable UI properties are updated on the main thread after
                 * the preference operation has completed.
                 */
                updateButtonStates()

                Log.d(TAG, "DEFCON Group ID left: $leftGroupId")
            } catch (exception: Exception) {
                Log.e(TAG, "Error leaving group", exception)
            }
        }
    }

    /**
     * Processes the QR code scanner result.
     *
     * A null or blank result represents a cancelled or empty scan. In that
     * situation, no stored group information is changed.
     *
     * @param groupIdToJoin The scanned group ID, or null when scanning was cancelled.
     */
    fun processQrCodeResult(groupIdToJoin: String?) {
        val normalizedGroupId = groupIdToJoin?.trim()

        if (normalizedGroupId.isNullOrEmpty()) {
            Log.d(TAG, "QR code scan was cancelled or returned no content")
            return
        }

        viewModelScope.launch {
            try {
                val installationId = FirebaseInstallations.getInstance().id.await()

                withContext(Dispatchers.IO) {
                    firebase.firestore.joinDefconGroup(
                        normalizedGroupId,
                        installationId
                    )
                }

                core.preferences?.joinedDefconGroupId = normalizedGroupId
                core.liveDataManager?.emitDefconGroupChange(
                    "",
                    normalizedGroupId,
                    GroupPreferenceViewModel::class.java
                )

                updateButtonStates()

                Log.d(TAG, "Successfully joined group: $normalizedGroupId")
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Error joining group: $normalizedGroupId",
                    exception
                )
            }
        }
    }

    /**
     * Updates the UI based on messages from background components.
     *
     * @param inputMessage The message containing update information.
     */
    override fun updateView(inputMessage: Message) {
        // No message-based UI updates are currently required.
    }

    /**
     * Updates the group management button states from the persisted group IDs.
     */
    private fun updateButtonStates() {
        val createdGroupId = core.preferences?.createdDefconGroupId.orEmpty()
        val joinedGroupId = core.preferences?.joinedDefconGroupId.orEmpty()

        textToEncode = when {
            createdGroupId.isNotEmpty() -> createdGroupId
            joinedGroupId.isNotEmpty() -> joinedGroupId
            else -> ""
        }

        isCreateGroupButtonEnabled =
            createdGroupId.isEmpty() && joinedGroupId.isEmpty()

        isDeleteGroupButtonEnabled = createdGroupId.isNotEmpty()

        isJoinGroupButtonEnabled =
            createdGroupId.isEmpty() && joinedGroupId.isEmpty()

        isLeaveGroupButtonEnabled = joinedGroupId.isNotEmpty()
    }

    private companion object {
        const val TAG = "GroupPreferenceVM"
    }
}
