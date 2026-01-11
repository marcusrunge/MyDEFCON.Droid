package com.marcusrunge.mydefcon.core.interfaces

import kotlinx.coroutines.flow.SharedFlow

/**
 * An interface for managing live data within the application.
 *
 * This interface provides a contract for emitting and observing real-time data streams,
 * specifically for the DEFCON status and group changes.
 */
interface LiveDataManager {
    /**
     * A [SharedFlow] that emits pairs of DEFCON status and the source class that triggered the update.
     *
     * Components can collect from this flow to react to changes in the DEFCON status in real-time.
     * The pair consists of the new DEFCON status (an [Int]) and the [Class] of the component
     * that initiated the change.
     */
    val defconStatusFlow: SharedFlow<Pair<Int, Class<*>>>

    /**
     * A [SharedFlow] that emits changes to the DEFCON group.
     *
     * Components can collect from this flow to react to changes in the DEFCON group in real-time.
     * The triple consists of the created DEFCON group ID (a [String]), the joined DEFCON group
     * ID (a [String]) and the [Class] of the component that initiated the change.
     */
    val defconGroupChangeFlow: SharedFlow<Triple<String, String, Class<*>>>

    /**
     * Emits a new DEFCON status to the [defconStatusFlow].
     *
     * @param status The new DEFCON status to emit.
     * @param source The class that is emitting the new status.
     */
    fun emitDefconStatus(status: Int, source: Class<*>)

    /**
     * Emits a new DEFCON group change to the [defconGroupChangeFlow].
     *
     * @param createdGroupId The created DEFCON group ID.
     * @param joinedGroupId The joined DEFCON group ID.
     * @param source The class that is emitting the new status.
     */
    fun emitDefconGroupChange(createdGroupId: String, joinedGroupId: String, source: Class<*>)
}
