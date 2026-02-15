package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.Broadcast
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.DefconStatusManager
import com.marcusrunge.mydefcon.core.interfaces.LiveDataManager
import com.marcusrunge.mydefcon.core.interfaces.Preferences
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

/**
 * An abstract base class that provides a foundational implementation of the [Core] interface.
 *
 * This class is designed to be extended by concrete implementations of the core module.
 * It manages the lifecycle of core components and provides access to low-level operations
 * like preferences and broadcasts. Subclasses are responsible for initializing the
 * `lateinit` properties, which are then exposed through the [Core] interface.
 *
 * @param _preferencesOperations An implementation of [PreferencesOperations] for handling persistent data.
 * @param _broadcastOperations An implementation of [BroadcastOperations] for sending system-wide broadcasts.
 */
internal abstract class CoreBase(
    private val _preferencesOperations: PreferencesOperations,
    private val _broadcastOperations: BroadcastOperations
) : Core {

    // Backing properties for the core components.
    // These are intended to be initialized in the subclass.
    protected lateinit var _preferences: Preferences
    protected lateinit var _broadcast: Broadcast
    protected lateinit var _liveDataManager: LiveDataManager
    protected lateinit var _defconStatusManager: DefconStatusManager
    protected lateinit var _coroutineScope: CoroutineScope

    /**
     * A coroutine job that is a [SupervisorJob].
     * This ensures that the failure of one child coroutine does not cause the others to fail,
     * which is crucial for the stability of the core components.
     */
    protected val job = SupervisorJob()

    /** Provides access to the [Preferences] component. */
    override val preferences: Preferences?
        get() = _preferences

    /** Provides access to the [Broadcast] component. */
    override val broadCast: Broadcast?
        get() = _broadcast

    /** Provides access to the [LiveDataManager] component. */
    override val liveDataManager: LiveDataManager?
        get() = _liveDataManager

    /** Provides access to the [DefconStatusManager] component. */
    override val defconStatusManager: DefconStatusManager?
        get() = _defconStatusManager

    /** Provides a [CoroutineScope] for managing asynchronous operations. */
    override val coroutineScope: CoroutineScope?
        get() = _coroutineScope

    /** Provides internal access to the [PreferencesOperations] for use within the core module. */
    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations

    /** Provides internal access to the [BroadcastOperations] for use within the core module. */
    internal val broadcastOperations: BroadcastOperations get() = _broadcastOperations
}