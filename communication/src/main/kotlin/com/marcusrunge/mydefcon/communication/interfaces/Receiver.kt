package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem

interface Receiver {
    fun addOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener)
    fun removeOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener)
    fun addOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener)
    fun removeOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener)
}

interface OnDefconStatusReceivedListener {
    /**
     * Occurs when a new defcon status has been issued.
     * @param status the defcon status
     */
    fun onDefconStatusReceived(status: Int)
}

interface OnCheckItemsReceivedListener {
    /**
     * Occurs when check items have been received.
     * @param checkItems the check items.
     */
    fun onCheckItemsReceived(checkItems: List<CheckItem>)
}

internal interface OnReceived {
    fun onCheckItemsReceived(checkItems: List<CheckItem>)
    fun onDefconStatusReceived(status: Int)
}