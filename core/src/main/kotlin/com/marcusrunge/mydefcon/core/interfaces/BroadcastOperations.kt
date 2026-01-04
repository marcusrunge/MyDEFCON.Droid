package com.marcusrunge.mydefcon.core.interfaces

/**
 * Defines the contract for sending broadcast messages within the application.
 * This interface provides a standardized way to handle broadcasts, making it easier to manage
 * and test components that rely on broadcasting functionality.
 */
interface BroadcastOperations {
    /**
     * Sends a broadcast to the MyDefcon widget.
     * @param action The action to be performed by the widget.
     * @param data The extra data to be sent with the broadcast.
     */
    fun sendBroadcastToMyDefconWidget(action: String?, data: String?)
}
