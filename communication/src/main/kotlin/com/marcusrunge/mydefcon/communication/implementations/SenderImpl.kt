package com.marcusrunge.mydefcon.communication.implementations

import android.net.LinkAddress
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Sender
import com.marcusrunge.mydefcon.communication.models.CheckItemsMessage
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress


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
        val message = DefconMessage(status)
        base.defconStatusMessageUuid = message.uuid
        val json = Json.encodeToString(message)
        val buffer = json.toByteArray(Charsets.UTF_8)
        val address = base.linkProperties?.linkAddresses.toInet4LinkAddress()
        val broadcast = address.toBroadcastAddress()
        val socket = DatagramSocket()
        val packet = DatagramPacket(buffer, buffer.size, broadcast, 4445)
        socket.send(packet)
    }

    override fun sendDefconCheckItems(checkItems: List<CheckItem>) {
        val message = CheckItemsMessage(checkItems)
        base.checkItemsMessageUuid = message.uuid
        val json = Json.encodeToString(message)
    }
}

private fun LinkAddress?.toBroadcastAddress(): InetAddress? {
    val prefix=this?.prefixLength
    val bytes =this?.address
    return this?.address
}

private fun List<LinkAddress>?.toInet4LinkAddress(): LinkAddress? {
    this?.forEach {
        if (it.address is Inet4Address) return (it)
    }
    return null
}
