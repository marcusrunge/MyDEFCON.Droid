package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import android.util.Log
import androidx.databinding.Bindable
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.marcusrunge.mydefcon.BR
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        val createdGroupId = core.preferences.createdDefconGroupId
        val joinedGroupId = core.preferences.joinedDefconGroupId

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
                    core.preferences.createdDefconGroupId = defconGroupId
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
                if (core.preferences.createdDefconGroupId.isNotEmpty()) {
                    firebase.firestore.deleteDefconGroup(core.preferences.createdDefconGroupId)
                    core.preferences.createdDefconGroupId = ""
                    updateButtonStates()
                }
            } catch (e: Exception) {
                // Handle exceptions from Firebase
                Log.e("GroupPreferenceViewModel", "Error during group deletion", e)
            }
        }
    }

    fun onJoinGroupClicked() {
        // This might involve showing a dialog to enter a group ID.
        // For now, let's assume the ID to join is in 'textToEncode' or from another input.
        // You'll need a way for the user to input the group ID they want to join.
        // Let's assume you have a separate EditText for the group ID to join,
        // or you re-purpose `textToEncode` if that's your design.

        // val groupIdToJoin = textToEncode // Or from another LiveData bound to an EditText
        // if (groupIdToJoin.isNotEmpty()) {
        //     viewModelScope.launch {
        //         // val success = firebase.joinGroup(groupIdToJoin)
        //         // if (success) {
        //         //    core.preferences.joinedDefconGroupId = groupIdToJoin
        //         //    updateButtonStates()
        //         // } else {
        //         //    // Handle error
        //         // }
        //     }
        // } else {
        //     // Prompt user to enter a group ID
        // }
    }

    fun onLeaveGroupClicked() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    firebase.firestore.leaveDefconGroup(
                        core.preferences.createdDefconGroupId,
                        core.preferences.fcmRegistrationToken
                    )
                    core.preferences.joinedDefconGroupId = ""
                    updateButtonStates()
                    Log.d(
                        "GroupPreference",
                        "DEFCON Group ID left: $core.preferences.createdDefconGroupId"
                    )
                }
            } catch (e: Exception) {
                // Handle exceptions from Firebase
                Log.e("GroupPreference", "Error during group deletion", e)
            }
        }
    }
}