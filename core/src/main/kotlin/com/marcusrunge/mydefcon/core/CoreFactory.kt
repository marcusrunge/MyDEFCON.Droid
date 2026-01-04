package com.marcusrunge.mydefcon.core

import com.marcusrunge.mydefcon.core.implementations.CoreImpl
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import kotlin.jvm.Volatile

/**
 * Factory for creating the [Core] instance.
 */
interface CoreFactory {
    /**
     * Creates the core instance.
     * @param preferencesOperations an instance of [PreferencesOperations].
     * @param broadcastOperations an instance of [BroadcastOperations].
     * @return The singleton [Core] instance.
     * @see Core
     */
    fun create(
        preferencesOperations: PreferencesOperations,
        broadcastOperations: BroadcastOperations
    ): Core
}

/**
 * Implementation of [CoreFactory] that provides a singleton [Core] instance.
 * This factory ensures that only one instance of the core is created and used throughout the application.
 */
class CoreFactoryImpl {
    /**
     * Companion object to implement the [CoreFactory] interface and provide a singleton instance of [Core].
     */
    companion object : CoreFactory {
        @Volatile
        private var core: Core? = null

        /**
         * Creates and returns a singleton instance of the [Core].
         * This implementation uses a thread-safe, double-checked locking pattern to ensure that only one instance of [Core] is created.
         *
         * @param preferencesOperations The operations for managing preferences.
         * @param broadcastOperations The operations for sending broadcast messages.
         * @return The singleton [Core] instance.
         */
        override fun create(
            preferencesOperations: PreferencesOperations,
            broadcastOperations: BroadcastOperations
        ): Core {
            // First check, if the instance is already created, return it.
            // This is to avoid the expensive synchronized block every time.
            return core ?: synchronized(this) {
                // Second check, in case another thread initialized the instance
                // while this thread was waiting for the lock.
                core ?: CoreImpl(broadcastOperations, preferencesOperations).also {
                    // Create the instance and assign it.
                    core = it
                }
            }
        }
    }
}
