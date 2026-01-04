package com.marcusrunge.mydefcon.core.interfaces

/**
 * Defines the contract for sending broadcast messages within the application.
 * This interface provides a standardized way to handle broadcasts, making it easier to manage
 * and test components that rely on broadcasting functionality.
 */
interface Broadcast {
    /**
     * Sends a DEFCON status update broadcast.
     * This method is responsible for broadcasting the current DEFCON status to other parts of the application.
     *
     * @param defcon The DEFCON level to broadcast.
     * @param source The class that is the source of the broadcast. This can be used by receivers
     * to identify the origin of the broadcast and handle it accordingly.
     */
    fun sendDefconBroadcast(defcon: Int, source: Class<*>)
}
