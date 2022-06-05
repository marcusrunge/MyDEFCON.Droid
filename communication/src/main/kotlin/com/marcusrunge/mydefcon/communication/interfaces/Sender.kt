package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem

interface Sender {
    /**
     * Sends a defcon status to udp listeners.
     */
    suspend fun sendDefconStatus(status: Int)
    /**
     * Sends a list of check items to udp listeners.
     */
    suspend fun sendDefconCheckItems(checkItems: List<CheckItem>)
}