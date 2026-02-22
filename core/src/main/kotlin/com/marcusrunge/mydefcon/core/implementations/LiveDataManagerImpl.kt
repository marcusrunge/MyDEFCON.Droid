package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.LiveDataManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * An implementation of the [LiveDataManager] interface.
 *
 * This class is responsible for managing the app's live data streams, specifically the
 * DEFCON status and group changes. It provides a mechanism to emit new statuses and allows
 * other components to observe these changes in real-time. It is implemented as a singleton
 * to ensure a single, consistent source of live data.
 *
 * @param coreBase The core base dependency providing access to the application's coroutine scope.
 */
internal class LiveDataManagerImpl(private val coreBase: CoreBase) : LiveDataManager {
    /**
     * A private, mutable shared flow that is used to emit DEFCON status updates internally.
     * The pair consists of the new DEFCON status (an [Int]) and the [Class] of the component
     * that initiated the change.
     */
    private val _defconStatusFlow = MutableSharedFlow<Pair<Int, Class<*>>>()

    /**
     * A private, mutable shared flow that is used to emit checklist changes internally.
     * The value is the [Class] of the component that initiated the change.
     */
    private val _checkListChangeFlow = MutableSharedFlow<Class<*>>()

    /**
     * A private, mutable shared flow that is used to emit DEFCON group changes internally.
     * The triple consists of the created DEFCON group ID (a [String]), the joined DEFCON group
     * ID (a [String]) and the [Class] of the component that initiated the change.
     */
    private val _defconGroupChangeFlow = MutableSharedFlow<Triple<String, String, Class<*>>>()

    /**
     * A public, immutable shared flow that exposes the DEFCON status updates to observers.
     * This follows the best practice of exposing only immutable flows to consumers.
     */
    override val defconStatusFlow: SharedFlow<Pair<Int, Class<*>>>
        get() = _defconStatusFlow.asSharedFlow()

    /**
     * A public, immutable shared flow that exposes checklist changes to observers.
     * This follows the best practice of exposing only immutable flows to consumers.
     */
    override val checkListChangeFlow: SharedFlow<Class<*>>
        get() = _checkListChangeFlow.asSharedFlow()

    /**
     * A public, immutable shared flow that exposes the DEFCON group changes to observers.
     * This follows the best practice of exposing only immutable flows to consumers.
     */
    override val defconGroupChangeFlow: SharedFlow<Triple<String, String, Class<*>>>
        get() = _defconGroupChangeFlow.asSharedFlow()

    /**
     * Emits a new DEFCON status to the shared flow.
     *
     * This function is called by other components when they need to update the global
     * DEFCON status. It launches a coroutine in the core's coroutine scope to emit
     * the new status to the [_defconStatusFlow].
     *
     * @param status The new DEFCON status to emit.
     * @param source The class that is emitting the new status.
     */
    override fun emitDefconStatus(status: Int, source: Class<*>) {
        coreBase.coroutineScope?.launch {
            _defconStatusFlow.emit(Pair(status, source))
        }
    }

    /**
     * Emits a new checklist change to the shared flow.
     *
     * This function is called by other components when they need to signal that the
     * checklist has been modified. It launches a coroutine in the core's coroutine scope to emit
     * the new status to the [_checkListChangeFlow].
     *
     * @param source The class that is emitting the change.
     */
    override fun emitCheckListChange(source: Class<*>) {
        coreBase.coroutineScope?.launch {
            _checkListChangeFlow.emit(source)
        }
    }

    /**
     * Emits a new DEFCON group change to the shared flow.
     *
     * This function is called by other components when they need to update the global
     * DEFCON group. It launches a coroutine in the core's coroutine scope to emit
     * the new status to the [_defconGroupChangeFlow].
     *
     * @param createdGroupId The created DEFCON group ID.
     * @param joinedGroupId The joined DEFCON group ID.
     * @param source The class that is emitting the new status.
     */
    override fun emitDefconGroupChange(
        createdGroupId: String,
        joinedGroupId: String,
        source: Class<*>
    ) {
        coreBase.coroutineScope?.launch {
            _defconGroupChangeFlow.emit(Triple(createdGroupId, joinedGroupId, source))
        }
    }

    internal companion object {
        @Volatile
        private var instance: LiveDataManager? = null

        /**
         * Creates and returns a singleton instance of the [LiveDataManager].
         *
         * This function ensures that only one instance of [LiveDataManagerImpl] is created
         * and used throughout the application. It uses a thread-safe, double-checked locking
         * mechanism for robust singleton implementation.
         *
         * @param coreBase The [CoreBase] instance required for the [LiveDataManagerImpl] constructor.
         * @return The singleton instance of [LiveDataManager].
         */
        fun create(coreBase: CoreBase): LiveDataManager =
            instance ?: synchronized(this) {
                instance ?: LiveDataManagerImpl(coreBase).also { instance = it }
            }
    }
}
