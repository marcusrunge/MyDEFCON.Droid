package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem
import java.net.InetAddress

interface Server {
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
    suspend fun startUdpServer()

    /**
     * Stops listening for defcon status changes.
     */
    suspend fun stopUdpServer()

    /**
     * Starts listening for check items synchronisation.
     */
    suspend fun startCheckItemsSyncListener()

    /**
     * Stops listening for check items synchronisation.
     */
    suspend fun stopTcpServer()
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
    suspend fun onRequestReceived(requestCode: Int, address: InetAddress)
}