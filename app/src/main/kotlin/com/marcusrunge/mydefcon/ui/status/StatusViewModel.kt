package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.services.ForegroundSocketService
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    application: Application, core: Core
) : ObservableViewModel(application), ServiceConnection {
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    private var foregroundSocketService: ForegroundSocketService? = null

    init {
        when (core.preferences.status) {
            1 -> _checkedRadioButtonId.value = R.id.radio_defcon1
            2 -> _checkedRadioButtonId.value = R.id.radio_defcon2
            3 -> _checkedRadioButtonId.value = R.id.radio_defcon3
            4 -> _checkedRadioButtonId.value = R.id.radio_defcon4
            else -> _checkedRadioButtonId.value = R.id.radio_defcon5
        }
    }

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
        foregroundSocketService = (p1 as ForegroundSocketService.LocalBinder).getService()
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        foregroundSocketService = null
    }
}