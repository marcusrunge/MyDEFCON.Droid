package com.marcusrunge.mydefcon.core.interfaces

interface BroadcastOperations {
    /**
     * Sends a broadcast
     * @param action The action
     * @param data The extra data
     */
    fun sendBroadcast(action: String?, data: String?)
}