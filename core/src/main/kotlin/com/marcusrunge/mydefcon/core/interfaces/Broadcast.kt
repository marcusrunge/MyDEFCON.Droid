package com.marcusrunge.mydefcon.core.interfaces

interface Broadcast {
    /**
     * Sends a DEFCON status update broadcast
     */
    fun sendDefconBroadcast(defcon:Int)
}