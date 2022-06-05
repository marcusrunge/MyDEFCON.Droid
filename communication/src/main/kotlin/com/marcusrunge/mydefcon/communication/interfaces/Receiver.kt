package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem

interface Receiver {
    /**
     * Adds a defcon status received listener.
     */
    fun addOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener)

    /**
     * Removes a defcon status received listener.
     */
    fun removeOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener)

    /**
     * Adds a check items received listener.
     */
    fun addOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener)

    /**
     * Removes a check items received listener.
     */
    fun removeOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener)

    /**
     * Starts listening for defcon status changes.
     */
    suspend fun startDefconStatusChangeListener()

    /**
     * Stops listening for defcon status changes.
     */
    suspend fun stopDefconStatusChangeListener()

    /**
     * Starts listening for check items synchronisation.
     */
    suspend fun startCheckItemsSyncListener()
    /**
     * Stops listening for check items synchronisation.
     */
    suspend fun stopCheckItemsSyncListener()
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