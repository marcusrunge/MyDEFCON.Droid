package com.marcusrunge.mydefcon.communication.implementations

import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Sender
import com.marcusrunge.mydefcon.communication.models.CheckItemsMessage
import com.marcusrunge.mydefcon.communication.models.DefconMessage
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
        val message=DefconMessage(status)
        base.defconStatusMessageUuid=message.uuid
    }

    override fun sendDefconCheckItems(checkItems: List<CheckItem>) {
        val message=CheckItemsMessage(checkItems)
        base.checkItemsMessageUuid=message.uuid
    }
}