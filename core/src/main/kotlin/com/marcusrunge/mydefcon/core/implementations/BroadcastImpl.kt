package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Broadcast

/**
 * Implementation of the [Broadcast] interface.
 *
 * This class provides the functionality for sending broadcasts related to DEFCON status changes.
 * It is implemented as a singleton to ensure a single point of broadcast management.
 *
 * @param coreBase The core base dependency providing access to broadcast operations.
 */
internal class BroadcastImpl(private val coreBase: CoreBase) : Broadcast {
    /**
     * Sends a broadcast to update the DEFCON level.
     *
     * This method utilizes the broadcast operations from [coreBase] to send an intent
     * with the action "com.marcusrunge.mydefcon.DEFCON_UPDATE" and the new DEFCON level as a string extra.
     * This is typically used to update widgets or other components of the application.
     *
     * @param defcon The new DEFCON level to be broadcasted.
     * @param source The class that initiated the DEFCON level change. This parameter is currently unused but
     *               could be useful for logging or more complex broadcast logic in the future.
     */
    override fun sendDefconBroadcast(defcon: Int, source: Class<*>) {
        coreBase.broadcastOperations.sendBroadcastToMyDefconWidget(
            "com.marcusrunge.mydefcon.DEFCON_UPDATE",
            defcon.toString()
        )
    }

    internal companion object {
        @Volatile
        private var instance: Broadcast? = null

        /**
         * Creates and returns a singleton instance of the [Broadcast] interface.
         *
         * This function ensures that only one instance of [BroadcastImpl] is created and used
         * throughout the application. It uses a thread-safe, double-checked locking mechanism.
         *
         * @param coreBase The [CoreBase] instance required for the [BroadcastImpl] constructor.
         * @return The singleton instance of [Broadcast].
         */
        fun create(coreBase: CoreBase): Broadcast =
            instance ?: synchronized(this) {
                instance ?: BroadcastImpl(coreBase).also { instance = it }
            }
    }
}