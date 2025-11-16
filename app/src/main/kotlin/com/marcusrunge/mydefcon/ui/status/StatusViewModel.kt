package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.os.Message
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    val app: Application,
    val core: Core
) : ObservableViewModel(app) {

    private val _isDefcon1ButtonChecked = MutableStateFlow(false)
    val isDefcon1ButtonChecked = _isDefcon1ButtonChecked.asStateFlow()

    private val _isDefcon2ButtonChecked = MutableStateFlow(false)
    val isDefcon2ButtonChecked = _isDefcon2ButtonChecked.asStateFlow()

    private val _isDefcon3ButtonChecked = MutableStateFlow(false)
    val isDefcon3ButtonChecked = _isDefcon3ButtonChecked.asStateFlow()

    private val _isDefcon4ButtonChecked = MutableStateFlow(false)
    val isDefcon4ButtonChecked = _isDefcon4ButtonChecked.asStateFlow()

    private val _isDefcon5ButtonChecked = MutableStateFlow(false)
    val isDefcon5ButtonChecked = _isDefcon5ButtonChecked.asStateFlow()

    private val _checkedButtonId = MutableStateFlow<Int?>(null)
    val checkedButtonId = _checkedButtonId.asStateFlow()

    init {
        setDefconStatusButton(core.preferences?.status ?: 5)
    }

    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) {
            setDefconStatusButton(inputMessage.obj as Int)
        }
    }

    private fun setDefconStatusButton(status: Int) {
        _isDefcon1ButtonChecked.value = status == 1
        _isDefcon2ButtonChecked.value = status == 2
        _isDefcon3ButtonChecked.value = status == 3
        _isDefcon4ButtonChecked.value = status == 4
        _isDefcon5ButtonChecked.value = status == 5 || status !in 1..4
        // Consider updating _checkedButtonId here if needed by your UI
    }

    /**
     * This method should be called by the UI when the user selects a DEFCON status.
     */
    fun onDefconStatusSelected(status: Int) {
        if (core.preferences?.status == status) {
            return // No change
        }
        setDefconStatusButton(status) // Update UI immediately
        distributeDefconStatus(status)
    }

    private fun distributeDefconStatus(status: Int) {
        core.preferences?.status = status
        core.liveDataManager?.emitDefconStatus(status, StatusViewModel::class.java)
    }
}