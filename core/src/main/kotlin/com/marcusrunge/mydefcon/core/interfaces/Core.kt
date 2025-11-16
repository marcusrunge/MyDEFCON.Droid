package com.marcusrunge.mydefcon.core.interfaces

import kotlinx.coroutines.CoroutineScope

interface Core {
    /**
     * Gets @see BroadCast
     */
    val broadCast: Broadcast?

    /**
     * Gets @see Preferences
     */
    val preferences: Preferences?

    /**
     * Gets @see LiveDataManager
     */
    val liveDataManager: LiveDataManager?

    /**
     * Gets @see DefconStatusHandler
     */
    val defconStatusManager: DefconStatusManager?

    /**
     * Gets @see CoroutineScope
     */
    val coroutineScope: CoroutineScope?
}