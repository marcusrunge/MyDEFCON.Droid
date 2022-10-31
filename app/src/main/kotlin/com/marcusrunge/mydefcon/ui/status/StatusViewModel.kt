package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.content.IntentFilter
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.receiver.DefconStatusReceiver
import com.marcusrunge.mydefcon.receiver.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val app: Application, core: Core
) : ObservableViewModel(app), DefaultLifecycleObserver, OnDefconStatusReceivedListener {
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    private var receiver: DefconStatusReceiver = DefconStatusReceiver()
    private var _listener: OnInterruptedListener? = null

    init {
        setDefconStatus(core.preferences.status)
        receiver.setOnDefconStatusReceivedListener(this)
        val filter = IntentFilter("com.marcusrunge.mydefcon.DEFCONSTATUS_RECEIVED")
        LocalBroadcastManager.getInstance(app).registerReceiver(receiver, filter)
    }

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId

    /**
     * Sets an interrupted listener.
     */
    fun setOnInterruptedListener(listener: OnInterruptedListener) {
        _listener = listener
    }

    /**
     * Removes the interrupted listener.
     */
    fun removeOnInterruptedListener() {
        _listener = null
    }

    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) setDefconStatus(
            inputMessage.obj as Int
        )
    }

    private fun setDefconStatus(status: Int) {
        _listener?.onInterrupted(true)
        when (status) {
            1 -> _checkedRadioButtonId.value = R.id.radio_defcon1
            2 -> _checkedRadioButtonId.value = R.id.radio_defcon2
            3 -> _checkedRadioButtonId.value = R.id.radio_defcon3
            4 -> _checkedRadioButtonId.value = R.id.radio_defcon4
            else -> _checkedRadioButtonId.value = R.id.radio_defcon5
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        receiver.removeOnDefconStatusReceivedListener()
        LocalBroadcastManager.getInstance(app).unregisterReceiver(receiver)
        super.onDestroy(owner)
    }

    override fun onDefconStatusReceived(status: Int, source: String?) {
        if (source != StatusFragment::class.java.canonicalName) {
            val setDefconStatusMessage = Message()
            setDefconStatusMessage.what = UPDATE_VIEW
            setDefconStatusMessage.obj = status
            handler.sendMessage(setDefconStatusMessage)
        }
    }
}

interface OnInterruptedListener {
    /**
     * Occurs when an interruption occurred.
     * @param status the interruption status.
     */
    fun onInterrupted(status: Boolean)
}