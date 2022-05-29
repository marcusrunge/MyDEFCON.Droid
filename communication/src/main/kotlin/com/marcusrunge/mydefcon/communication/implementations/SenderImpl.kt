package com.marcusrunge.mydefcon.communication.implementations

import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Sender
import com.marcusrunge.mydefcon.data.entities.CheckItem

internal class SenderImpl(private val base: NetworkBase) : Sender {
    internal companion object {
        private var instance: Sender? = null
        fun create(base: NetworkBase): Sender = when {
            instance != null -> instance!!
            else -> {
                instance = SenderImpl(base)
                instance!!
            }
        }
    }

    override fun sendDefconStatus(status: Int) {
        TODO("Not yet implemented")
    }

    override fun sendDefconCheckItems(checkItems: List<CheckItem>) {
        TODO("Not yet implemented")
    }
}