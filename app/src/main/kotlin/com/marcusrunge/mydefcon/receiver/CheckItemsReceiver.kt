package com.marcusrunge.mydefcon.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.marcusrunge.mydefcon.data.entities.CheckItem
import java.io.Serializable

class CheckItemsReceiver : BroadcastReceiver() {
    private var _listener: OnCheckItemsReceivedListener? = null

    @Suppress("UNCHECKED_CAST")
    override fun onReceive(p0: Context?, p1: Intent?) {
        if (p1?.action == "com.marcusrunge.mydefcon.CHECKITEMS_RECEIVED") {
            _listener?.onCheckItemsReceived(
                p1.getSerializableExtra(
                    "data",
                    Serializable::class.java
                ) as List<CheckItem>?
            )
        }
    }

    /**
     * Sets a defcon status received listener.
     */
    fun setOnCheckItemsReceivedListener(listener: OnCheckItemsReceivedListener) {
        _listener = listener
    }

    /**
     * Removes the defcon status received listener.
     */
    fun removeOnCheckItemsReceivedListener() {
        _listener = null
    }
}

interface OnCheckItemsReceivedListener {
    /**
     * Occurs when a status has been received.
     * @param status the status.
     */
    fun onCheckItemsReceived(items: List<CheckItem>?)
}