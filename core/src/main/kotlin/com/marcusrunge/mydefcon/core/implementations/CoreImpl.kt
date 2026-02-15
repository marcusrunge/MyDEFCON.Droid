package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * A concrete implementation of the [CoreBase] class.
 *
 * This class serves as the central hub for the application's core logic, bringing together
 * all the necessary components and managers. It is responsible for initializing and providing
 * access to the various features of the core module, such as preferences, broadcasting,
 * and data management.
 *
 * @param broadcastOperations An implementation of [BroadcastOperations] for sending system-wide broadcasts.
 * @param preferencesOperations An implementation of [PreferencesOperations] for handling persistent data.
 */
internal class CoreImpl(
    broadcastOperations: BroadcastOperations,
    preferencesOperations: PreferencesOperations
) : CoreBase(preferencesOperations, broadcastOperations) {

    /**
     * Initializes the core components of the application.
     *
     * The initialization block sets up the coroutine scope for background tasks and then
     * creates singleton instances for all the core managers. The order of initialization
     * is important, as some managers may depend on others. Finally, it kicks off the
     * [com.marcusrunge.mydefcon.core.interfaces.DefconStatusManager] by calling its initialize method.
     */
    init {
        // Create a coroutine scope for I/O operations, using the supervisor job from CoreBase.
        _coroutineScope = CoroutineScope(Dispatchers.IO + job)

        // Initialize the singleton instances of the core components.
        // The 'create' methods use a singleton pattern to ensure only one instance of each manager exists.
        _broadcast = BroadcastImpl.create(this)
        _checkItemsSyncManager = CheckItemsSyncManagerImpl.create(this)
        _defconStatusManager = DefconStatusManagerImpl.create(this)
        _liveDataManager = LiveDataManagerImpl.create(this)
        _preferences = PreferencesImpl.create(this)

        // After all managers are initialized, start the DefconStatusManager's logic.
        _defconStatusManager.initialize()
    }
}
