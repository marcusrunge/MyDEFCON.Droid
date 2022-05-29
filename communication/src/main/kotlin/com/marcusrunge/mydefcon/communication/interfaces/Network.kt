package com.marcusrunge.mydefcon.communication.interfaces

interface Network {
    /**
     * Provides receiver related functionality.
     */
    val receiver: Receiver

    /**
     * Provides sender related functionality.
     */
    val sender: Sender
}