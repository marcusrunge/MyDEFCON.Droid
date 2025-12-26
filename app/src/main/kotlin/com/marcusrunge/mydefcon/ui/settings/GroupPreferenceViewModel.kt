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

@HiltViewModel
class GroupPreferenceViewModel @Inject constructor(
    app: Application, private val core: Core, private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {
    private var _textToEncode: String = ""
    private var _isCreateGroupButtonEnabled: Boolean = false
    private var _isDeleteGroupButtonEnabled: Boolean = false
    private var _isJoinGroupButtonEnabled: Boolean = false
    private var _isLeaveGroupButtonEnabled: Boolean = false
    private val _scanQrCodeEvent = MutableLiveData<Boolean>()
    val scanQrCodeEvent: LiveData<Boolean>
        get() = _scanQrCodeEvent
    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    @get:Bindable
    var textToEncode: String
        get() = _textToEncode
        set(value) {
            _textToEncode = value
            notifyPropertyChanged(BR.textToEncode)
        }

    @get:Bindable
    var isCreateGroupButtonEnabled: Boolean
        get() = _isCreateGroupButtonEnabled
        set(value) {
            _isCreateGroupButtonEnabled = value
            notifyPropertyChanged(BR.createGroupButtonEnabled)
        }

    @get:Bindable
    var isDeleteGroupButtonEnabled: Boolean
        get() = _isDeleteGroupButtonEnabled
        set(value) {
            _isDeleteGroupButtonEnabled = value
            notifyPropertyChanged(BR.deleteGroupButtonEnabled)
        }

    @get:Bindable
    var isJoinGroupButtonEnabled: Boolean
        get() = _isJoinGroupButtonEnabled
        set(value) {
            _isJoinGroupButtonEnabled = value
            notifyPropertyChanged(BR.joinGroupButtonEnabled)
        }

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

    private fun updateButtonStates() {
        val createdGroupId = core.preferences!!.createdDefconGroupId
        val joinedGroupId = core.preferences!!.joinedDefconGroupId

        textToEncode = createdGroupId // Or logic to display joined group ID if preferred

        if (createdGroupId.isEmpty() && joinedGroupId.isEmpty()) {
            isCreateGroupButtonEnabled = true
            isDeleteGroupButtonEnabled = false
            isJoinGroupButtonEnabled = true
            isLeaveGroupButtonEnabled = false
        } else if (createdGroupId.isNotEmpty() && joinedGroupId.isEmpty()) {
            isCreateGroupButtonEnabled = false
            isDeleteGroupButtonEnabled = true
            isJoinGroupButtonEnabled = false
            isLeaveGroupButtonEnabled = false
        } else if (createdGroupId.isEmpty() && joinedGroupId.isNotEmpty()) {
            textToEncode = joinedGroupId // Show the joined group ID
            isCreateGroupButtonEnabled = false
            isDeleteGroupButtonEnabled = false
            isJoinGroupButtonEnabled = false
            isLeaveGroupButtonEnabled = true
        }
    }

    fun onCreateGroupClicked() {
        viewModelScope.launch {
            try {
                val defconGroupId = withContext(Dispatchers.IO) {
                    firebase.firestore.createDefconGroup()
                }
                Log.d("GroupPreferenceViewModel", "DEFCON Group ID created: $defconGroupId")
                if (defconGroupId.isNotEmpty()) {
                    core.preferences!!.createdDefconGroupId = defconGroupId
                    textToEncode = defconGroupId
                    updateButtonStates()
                } else {
                    Log.w("GroupPreferenceViewModel", "DEFCON Group ID is empty")
                }
            } catch (e: Exception) {
                Log.e("GroupPreferenceViewModel", "Error during group creation or QR generation", e)
            }
        }
    }

    fun onDeleteGroupClicked() {
        viewModelScope.launch {
            try {
                if (core.preferences!!.createdDefconGroupId.isNotEmpty()) {
                    firebase.firestore.deleteDefconGroup(core.preferences!!.createdDefconGroupId)
                    core.preferences!!.createdDefconGroupId = ""
                    updateButtonStates()
                }
            } catch (e: Exception) {
                // Handle exceptions from Firebase
                Log.e("GroupPreferenceViewModel", "Error during group deletion", e)
            }
        }
    }

    fun onJoinGroupClicked() {
        _scanQrCodeEvent.value = true
    }

    fun processQrCodeResult(groupIdToJoin: String?) {
        _scanQrCodeEvent.value = false // Reset the event
        if (groupIdToJoin.isNullOrEmpty()) {
            Log.w("GroupPreferenceViewModel", "QR code result was empty or null")
            // Optionally, show a message to the user
            return
        }

        viewModelScope.launch {
            var joinSuccess = false // Assume failure initially
            try {
                withContext(Dispatchers.IO) {
                    firebase.firestore.joinDefconGroup(groupIdToJoin,FirebaseInstallations.getInstance().id.await())
                }
                joinSuccess = true
                core.preferences!!.joinedDefconGroupId = groupIdToJoin
                updateButtonStates()
                Log.d("GroupPreferenceViewModel", "Successfully joined group: $groupIdToJoin")

            } catch (e: Exception) {
                joinSuccess = false
                Log.e("GroupPreferenceViewModel", "Error or failure joining group: $groupIdToJoin", e)
            }
        }
    }

    fun onLeaveGroupClicked() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    core.preferences!!.joinedDefconGroupId = ""
                    updateButtonStates()
                    Log.d(
                        "GroupPreference",
                        "DEFCON Group ID left: $core.preferences.createdDefconGroupId"
                    )
                }
            } catch (e: Exception) {
                Log.e("GroupPreference", "Error during group deletion", e)
            }
        }
    }
}