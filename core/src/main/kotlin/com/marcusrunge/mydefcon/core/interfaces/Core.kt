package com.marcusrunge.mydefcon.core.interfaces

interface Core {
    /**
     * Gets @see BroadCast
     */
    val broadCast: Broadcast

    /**
     * Gets @see Preferences
     */
    val preferences: Preferences
    /**
     * Gets @see LiveDataManager
     */
    val liveDataManager:LiveDataManager
}