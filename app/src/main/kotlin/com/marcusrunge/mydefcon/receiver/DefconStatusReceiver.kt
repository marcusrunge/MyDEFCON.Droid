package com.marcusrunge.mydefcon.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DefconStatusReceiver : BroadcastReceiver() {
    private var _listener: OnDefconStatusReceivedListener? = null
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1 != null && p1.action == "com.marcusrunge.mydefcon.DEFCONSTATUS_RECEIVED") {
            _listener?.onDefconStatusReceived(p1.getIntExtra("data", 5))
        }
    }

    /**
     * Sets a defcon status received listener.
     */
    fun setOnDefconStatusReceivedListener(listener: OnDefconStatusReceivedListener) {
        _listener = listener
    }

    /**
     * Removes the defcon status received listener.
     */
    fun removeOnDefconStatusReceivedListener() {
        _listener = null
    }
}

interface OnDefconStatusReceivedListener {
    /**
     * Occurs when a status has been received.
     * @param status the status.
     */
    fun onDefconStatusReceived(status: Int)
}