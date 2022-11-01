package com.marcusrunge.mydefcon.communication.implementations

import android.net.LinkAddress
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Client
import com.marcusrunge.mydefcon.communication.interfaces.OnCheckItemsReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.Synchronizer
import com.marcusrunge.mydefcon.communication.models.CheckItemsMessage
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.communication.models.RequestMessage
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.ref.WeakReference
import java.net.*
import java.util.stream.Collectors
import kotlin.math.pow


@Suppress("BlockingMethodInNonBlockingContext")
internal class ClientImpl(private val base: NetworkBase) : Client, Synchronizer {
    private val onCheckItemsReceivedListeners: MutableList<WeakReference<OnCheckItemsReceivedListener>> =
        mutableListOf()
    val statusSemaphore = Semaphore(permits = 1)
    val syncSemaphore = Semaphore(permits = 1)

    internal companion object {
        private var instance: Client? = null
        fun create(base: NetworkBase): Client = when {
            instance != null -> instance!!
            else -> {
                instance = ClientImpl(base)
                instance!!
            }
        }
    }

    override suspend fun sendDefconStatus(status: Int) {
        statusSemaphore.acquire()
        val message = DefconMessage(status)
        base.defconStatusMessageUuid = message.uuid
        val json = Json.encodeToString(message)
        val buffer = json.toByteArray(Charsets.UTF_8)
        val address = base.linkProperties?.linkAddresses.findInet4LinkAddress()
        val broadcast = address.calculateBroadcastAddress()
        withContext(Dispatchers.IO) {
            try {
                val socket = DatagramSocket()
                val packet = DatagramPacket(buffer, buffer.size, broadcast, 8888)
                socket.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                statusSemaphore.release()
            }
        }
    }

    override suspend fun requestSyncCheckItems() {
        syncSemaphore.acquire()
        val message = RequestMessage(NetworkImpl.CHECKITEMSSYNC_REQUESTCODE)
        base.requestMessageUuid = message.uuid
        val json = Json.encodeToString(message)
        val buffer = json.toByteArray(Charsets.UTF_8)
        val address = base.linkProperties?.linkAddresses.findInet4LinkAddress()
        val broadcast = address.calculateBroadcastAddress()
        withContext(Dispatchers.IO) {
            try {
                val socket = DatagramSocket()
                val packet = DatagramPacket(buffer, buffer.size, broadcast, 8888)
                socket.send(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                syncSemaphore.release()
            }
        }
    }

    override suspend fun syncCheckItems(address: InetAddress) {
        val socket = Socket(address, 8889)
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val json = reader.lines().collect(Collectors.joining())
            reader.close()
            val checkItemsMessage = Json.decodeFromString<CheckItemsMessage>(json)
            if (checkItemsMessage.uuid != base.checkItemsMessageUuid) onCheckItemsReceived(
                checkItemsMessage.checkItems
            )
        } catch (_: Exception) {
        } finally {
            socket.close()
        }
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
        for (weakRef in onCheckItemsReceivedListeners) {
            try {
                weakRef.get()?.onCheckItemsReceived(checkItems)
            } catch (_: Exception) {
            }
        }
    }

    override fun addOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener) {
        onCheckItemsReceivedListeners.add(WeakReference(onCheckItemsReceivedListener))
    }

    override fun removeOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener) {
        val iterator: MutableIterator<WeakReference<OnCheckItemsReceivedListener>> =
            onCheckItemsReceivedListeners.iterator()
        while (iterator.hasNext()) {
            val weakRef: WeakReference<OnCheckItemsReceivedListener> = iterator.next()
            if (weakRef.get() === onCheckItemsReceivedListener) {
                iterator.remove()
            }
        }
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
