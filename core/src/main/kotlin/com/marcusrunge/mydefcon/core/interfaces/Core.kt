package com.marcusrunge.mydefcon.core.interfaces

interface Core {
    /**
     * Gets @see Preferences
     */
    val preferences: Preferences

    /**
     * Gets @see Remote
     */
    val remote: Remote

    /**
     * Gets @see BroadCast
     */
    val broadCast: Broadcast
}