package com.marcusrunge.mydefcon.ui.status

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.services.ForegroundSocketService
import com.marcusrunge.mydefcon.services.OnReceivedListener
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    application: Application, core: Core
) : ObservableViewModel(application), ServiceConnection, OnReceivedListener {
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    @SuppressLint("StaticFieldLeak")
    private var foregroundSocketService: ForegroundSocketService? = null

    init {
        setDefconStatus(core.preferences.status)
    }

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId

    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) setDefconStatus(
            inputMessage.obj as Int
        )
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        foregroundSocketService = (p1 as ForegroundSocketService.LocalBinder).getService()
        foregroundSocketService?.addOnReceivedListener(this)
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        foregroundSocketService?.removeOnReceivedListener(this)
        foregroundSocketService = null
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
    }

    override fun onDefconStatusReceived(status: Int) {
        val setDefconStatusMessage = Message()
        setDefconStatusMessage.what = UPDATE_VIEW
        setDefconStatusMessage.obj = status
        handler.sendMessage(setDefconStatusMessage)
    }

    private fun setDefconStatus(status: Int) {
        when (status) {
            1 -> _checkedRadioButtonId.value = R.id.radio_defcon1
            2 -> _checkedRadioButtonId.value = R.id.radio_defcon2
            3 -> _checkedRadioButtonId.value = R.id.radio_defcon3
            4 -> _checkedRadioButtonId.value = R.id.radio_defcon4
            else -> _checkedRadioButtonId.value = R.id.radio_defcon5
        }
    }
}