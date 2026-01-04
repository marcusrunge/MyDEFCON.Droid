package com.marcusrunge.mydefcon.core.interfaces

import kotlinx.coroutines.CoroutineScope

/**
 * The central interface for accessing core functionalities of the application.
 *
 * This interface provides access to various managers and components that are essential
 * for the application's operation. Implementations of this interface will serve as a
 * central point of dependency injection for other parts of the app.
 */
interface Core {
    /**
     * Provides access to the [Broadcast] component for sending application-wide broadcasts.
     * Can be null if the broadcast feature is not available.
     */
    val broadCast: Broadcast?

    /**
     * Provides access to the [Preferences] component for managing application settings.
     * Can be null if the preferences feature is not available.
     */
    val preferences: Preferences?

    /**
     * Provides access to the [LiveDataManager] for managing real-time data.
     * Can be null if the live data management feature is not available.
     */
    val liveDataManager: LiveDataManager?

    /**
     * Provides access to the [DefconStatusManager] for handling DEFCON status logic.
     * Can be null if the DEFCON status management feature is not available.
     */
    val defconStatusManager: DefconStatusManager?

    /**
     * Provides a [CoroutineScope] for managing asynchronous operations within the core module.
     * Can be null if a custom coroutine scope is not provided.
     */
    val coroutineScope: CoroutineScope?
}
