package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Message
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.receiver.DefconStatusReceiver
import com.marcusrunge.mydefcon.receiver.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.services.ForegroundSocketService
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val app: Application, val core: Core, communication: Communication
) : ObservableViewModel(app), DefaultLifecycleObserver, OnDefconStatusReceivedListener {
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    private var receiver: DefconStatusReceiver = DefconStatusReceiver()
    private lateinit var statusViewModelOwner: LifecycleOwner
    private val checkedRadioButtonIdObserver = Observer<Int> {
        val status = when (it) {
            R.id.radio_defcon1 -> 1
            R.id.radio_defcon2 -> 2
            R.id.radio_defcon3 -> 3
            R.id.radio_defcon4 -> 4
            else -> 5
        }
        Intent(app.applicationContext, DefconStatusReceiver::class.java).also { intent ->
            intent.action = "com.marcusrunge.mydefcon.DEFCONSTATUS_SELECTED"
            intent.putExtra("data", status)
            intent.putExtra("source", StatusFragment::class.java.canonicalName)
            app.applicationContext?.let { ctx ->
                LocalBroadcastManager.getInstance(ctx).sendBroadcast(intent)
            }
        }
        viewModelScope.launch { communication.network.client.sendDefconStatus(status) }

    }

    init {
        receiver.setOnDefconStatusReceivedListener(this)
        val filter = IntentFilter("com.marcusrunge.mydefcon.DEFCONSTATUS_RECEIVED")
        LocalBroadcastManager.getInstance(app).registerReceiver(receiver, filter)
    }

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId

    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) setDefconStatus(
            inputMessage.obj as Int
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        statusViewModelOwner = owner
        _checkedRadioButtonId.observe(statusViewModelOwner, checkedRadioButtonIdObserver)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        receiver.removeOnDefconStatusReceivedListener()
        LocalBroadcastManager.getInstance(app).unregisterReceiver(receiver)
        _checkedRadioButtonId.removeObservers(owner)
        super.onDestroy(owner)
    }

    override fun onDefconStatusReceived(status: Int, source: String?) {
        if (source == ForegroundSocketService::class.java.canonicalName) {
            val setDefconStatusMessage = Message()
            setDefconStatusMessage.what = UPDATE_VIEW
            setDefconStatusMessage.obj = status
            handler.sendMessage(setDefconStatusMessage)
        }
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