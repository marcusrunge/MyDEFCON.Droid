package com.marcusrunge.mydefcon.communication.implementations

import android.net.LinkAddress
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Sender
import com.marcusrunge.mydefcon.communication.models.CheckItemsMessage
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.Inet4Address
import java.net.InetAddress
import kotlin.math.*


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
        /*val socket = DatagramSocket()
        val packet = DatagramPacket(buffer, buffer.size, broadcast, 4445)
        socket.send(packet)*/
    }

    override fun sendDefconCheckItems(checkItems: List<CheckItem>) {
        val message = CheckItemsMessage(checkItems)
        base.checkItemsMessageUuid = message.uuid
        val json = Json.encodeToString(message)
    }
}

private fun LinkAddress?.toBroadcastAddress(): InetAddress? {
    var prefix = this?.prefixLength
    val address = this?.address?.address
    if (prefix != null && address != null) {
        val subnetBytes =
            arrayOf(UByte.MAX_VALUE, UByte.MAX_VALUE, UByte.MAX_VALUE, UByte.MAX_VALUE)
        for (i in subnetBytes.indices) {
            if (prefix >= 8) {
                prefix -= 8
            } else {
                subnetBytes[i] = subnetBytes[i].toInt().xor((2.0.pow(((8 - prefix).toDouble())) -1).toInt()).toUByte()
                prefix -= prefix
            }
        }
        //TODO:Calculate Broadcast Address here
    }
    return null
}

private fun List<LinkAddress>?.toInet4LinkAddress(): LinkAddress? {
    this?.forEach {
        if (it.address is Inet4Address) return (it)
    }
    return null
}
