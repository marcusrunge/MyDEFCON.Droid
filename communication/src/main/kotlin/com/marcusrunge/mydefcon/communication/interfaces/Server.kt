package com.marcusrunge.mydefcon.communication.interfaces

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
    suspend fun startTcpServer()

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

internal interface OnReceived {
    fun onDefconStatusReceived(status: Int)
    suspend fun onRequestReceived(requestCode: Int, address: InetAddress)
}