package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.DefconStatusManager
import kotlinx.coroutines.launch

/**
 * An implementation of the [DefconStatusManager] interface.
 *
 * This class is responsible for managing the DEFCON status logic. It observes changes
 * in the DEFCON status from the [com.marcusrunge.mydefcon.core.interfaces.LiveDataManager] and triggers broadcasts accordingly.
 * It is implemented as a singleton to ensure a single source of truth for status management.
 *
 * @param coreBase The core base dependency providing access to other core components.
 */
internal class DefconStatusManagerImpl(private val coreBase: CoreBase) : DefconStatusManager {

    /**
     * Initializes the DEFCON status manager.
     *
     * This method launches a coroutine to collect DEFCON status updates from the
     * [com.marcusrunge.mydefcon.core.interfaces.LiveDataManager.defconStatusFlow]. When a new status is received, it uses the
     * [com.marcusrunge.mydefcon.core.interfaces.Broadcast] component to send a broadcast, effectively notifying other parts of the
     * application, like widgets, about the change.
     */
    override fun initialize() {
        coreBase.coroutineScope?.launch {
            coreBase.liveDataManager?.defconStatusFlow?.collect { pair ->
                // When a new DEFCON status is emitted, send a broadcast.
                coreBase.broadCast?.sendDefconBroadcast(
                    pair.first, // The new DEFCON level
                    pair.second // The class that initiated the change
                )
            }
        }
    }

    internal companion object {
        @Volatile
        private var instance: DefconStatusManager? = null

        /**
         * Creates and returns a singleton instance of the [DefconStatusManager].
         *
         * This function ensures that only one instance of [DefconStatusManagerImpl] is created
         * and used throughout the application. It uses a thread-safe, double-checked locking
         * mechanism.
         *
         * @param coreBase The [CoreBase] instance required for the [DefconStatusManagerImpl] constructor.
         * @return The singleton instance of [DefconStatusManager].
         */
        fun create(coreBase: CoreBase): DefconStatusManager =
            instance ?: synchronized(this) {
                instance ?: DefconStatusManagerImpl(coreBase).also { instance = it }
            }
    }
}
