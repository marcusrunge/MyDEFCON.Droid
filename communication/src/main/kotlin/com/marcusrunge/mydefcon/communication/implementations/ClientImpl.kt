package com.marcusrunge.mydefcon.communication.implementations

import android.net.LinkAddress
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Client
import com.marcusrunge.mydefcon.communication.interfaces.Synchronizer
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.communication.models.RequestMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import kotlin.math.pow


internal class SenderImpl(private val base: NetworkBase) : Client, Synchronizer {
    internal companion object {
        private var instance: Client? = null
        fun create(base: NetworkBase): Client = when {
            instance != null -> instance!!
            else -> {
                instance = SenderImpl(base)
                instance!!
            }
        }
    }

    override suspend fun sendDefconStatus(status: Int) {
        val message = DefconMessage(status)
        base.defconStatusMessageUuid = message.uuid
        val json = Json.encodeToString(message)
        val buffer = json.toByteArray(Charsets.UTF_8)
        val address = base.linkProperties?.linkAddresses.findInet4LinkAddress()
        val broadcast = address.calculateBroadcastAddress()
        withContext(Dispatchers.IO) {
            val socket = DatagramSocket()
            val packet = DatagramPacket(buffer, buffer.size, broadcast, 8888)
            socket.send(packet)
        }
    }

    override suspend fun requestSyncCheckItems() {
        val message = RequestMessage(NetworkImpl.CHECKITEMSSYNC_REQUESTCODE)
        base.requestMessageUuid = message.uuid
        val json = Json.encodeToString(message)
        val buffer = json.toByteArray(Charsets.UTF_8)
        val address = base.linkProperties?.linkAddresses.findInet4LinkAddress()
        val broadcast = address.calculateBroadcastAddress()
        withContext(Dispatchers.IO) {
            val socket = DatagramSocket()
            val packet = DatagramPacket(buffer, buffer.size, broadcast, 8888)
            socket.send(packet)
        }
    }

    override suspend fun syncCheckItems(address: InetAddress) {
        TODO("Not yet implemented")
    }
}

private fun LinkAddress?.calculateBroadcastAddress(): InetAddress? {
    var prefix = this?.prefixLength
    val address = this?.address?.address
    if (prefix != null && address != null) {
        val subnetBytes =
            arrayOf(UByte.MAX_VALUE, UByte.MAX_VALUE, UByte.MAX_VALUE, UByte.MAX_VALUE)
        val networkBytes =
            arrayOf(UByte.MIN_VALUE, UByte.MIN_VALUE, UByte.MIN_VALUE, UByte.MIN_VALUE)
        val broadcastBytes =
            arrayOf(UByte.MIN_VALUE, UByte.MIN_VALUE, UByte.MIN_VALUE, UByte.MIN_VALUE)
        for (i in subnetBytes.indices) {
            if (prefix >= 8) {
                prefix -= 8
            } else {
                subnetBytes[i] =
                    subnetBytes[i].toInt().xor((2.0.pow(((8 - prefix).toDouble())) - 1).toInt())
                        .toUByte()
                prefix -= prefix
            }
        }
        for (i in networkBytes.indices) {
            networkBytes[i] = address[i].toInt().and(subnetBytes[i].toInt()).toUByte()
        }
        for (i in broadcastBytes.indices) {
            broadcastBytes[i] = ((networkBytes[i] + subnetBytes[i].inv()).toUByte())
        }
        return InetAddress.getByAddress(
            byteArrayOf(
                broadcastBytes[0].toInt().toByte(),
                broadcastBytes[1].toInt().toByte(),
                broadcastBytes[2].toInt().toByte(),
                broadcastBytes[3].toInt().toByte()
            )
        )
    }
    return null
}

private fun List<LinkAddress>?.findInet4LinkAddress(): LinkAddress? {
    this?.forEach {
        if (it.address is Inet4Address) return (it)
    }
    return null
}
