package com.marcusrunge.mydefcon.communication.interfaces

interface Network {
    /**
     * Provides server related functionality.
     */
    val server: Server

    /**
     * Provides client related functionality.
     */
    val client: Client
}