package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem
import java.net.InetAddress

interface Client {
    /**
     * Sends a defcon status to udp listeners.
     */
    suspend fun sendDefconStatus(status: Int)

    /**
     * Sends a list of check items to udp listeners.
     */
    suspend fun requestSyncCheckItems()

    /**
     * Adds a check items received listener.
     */
    fun addOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener)

    /**
     * Removes a check items received listener.
     */
    fun removeOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener)

}

interface OnCheckItemsReceivedListener {
    /**
     * Occurs when check items have been received.
     * @param checkItems the check items.
     */
    fun onCheckItemsReceived(checkItems: List<CheckItem>)
}

internal interface Synchronizer {
    suspend fun syncCheckItems(address: InetAddress)
    fun onCheckItemsReceived(checkItems: List<CheckItem>)
}