package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Broadcast

internal class BroadcastImpl(private val coreBase: CoreBase) : Broadcast {
    internal companion object {
        var broadCast: Broadcast? = null
        fun create(coreBase: CoreBase): Broadcast = when {
            broadCast != null -> broadCast!!
            else -> {
                broadCast = BroadcastImpl(coreBase)
                broadCast!!
            }
        }
    }

    override fun sendDefconBroadcast(defcon: Int) {
        coreBase.broadcastOperations.sendBroadcast(
            "com.marcusrunge.mydefcon.DEFCON_UPDATE",
            defcon.toString()
        )
    }
}