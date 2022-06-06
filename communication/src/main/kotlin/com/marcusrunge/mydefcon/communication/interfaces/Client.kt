package com.marcusrunge.mydefcon.communication.interfaces

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
}

internal interface Synchronizer {
    suspend fun syncCheckItems(address: InetAddress)
}