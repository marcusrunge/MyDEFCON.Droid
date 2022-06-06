package com.marcusrunge.mydefcon.communication.implementations

import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.*
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.communication.models.RequestMessage
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.util.concurrent.locks.ReentrantLock

internal class ServerImpl(private val base: NetworkBase) : Server, OnReceived {
    private val onDefconStatusReceivedListeners: MutableList<WeakReference<OnDefconStatusReceivedListener>> =
        mutableListOf()
    private val onCheckItemsReceivedListeners: MutableList<WeakReference<OnCheckItemsReceivedListener>> =
        mutableListOf()
    private var udpServerJob: Job? = null
    private var tcpServerJob: Job? = null
    private val udpServerLock = ReentrantLock()
    private val tcpServerLock = ReentrantLock()

    internal companion object {
        private var instance: Server? = null
        fun create(base: NetworkBase): Server = when {
            instance != null -> instance!!
            else -> {
                instance = ServerImpl(base)
                instance!!
            }
        }
    }

    override fun addOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener) {
        onDefconStatusReceivedListeners.add(WeakReference(onDefconStatusReceivedListener))
    }

    override fun removeOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener) {
        val iterator: MutableIterator<WeakReference<OnDefconStatusReceivedListener>> =
            onDefconStatusReceivedListeners.iterator()
        while (iterator.hasNext()) {
            val weakRef: WeakReference<OnDefconStatusReceivedListener> = iterator.next()
            if (weakRef.get() === onDefconStatusReceivedListener) {
                iterator.remove()
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

    override suspend fun startUdpServer() {
        udpServerLock.lock()
        withContext(Dispatchers.IO) {
            udpServerJob = launch {
                val whileLock = ReentrantLock()
                while (true) {
                    whileLock.lock()
                    val buffer = ByteArray(256)
                    var socket: DatagramSocket? = null
                    try {
                        socket = DatagramSocket(8888)
                        socket.broadcast = true
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        val data = String(packet.data, 0, packet.length)
                        try {
                            val defconMessage = Json.decodeFromString<DefconMessage>(data)
                            if (defconMessage.uuid != base.defconStatusMessageUuid) onDefconStatusReceived(
                                defconMessage.status
                            )
                        } catch (e: Exception) {
                            val requestMessage = Json.decodeFromString<RequestMessage>(data)
                            if (requestMessage.uuid != base.requestMessageUuid) onRequestReceived(
                                requestMessage.requestCode,
                                packet.address
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        socket?.close()
                        whileLock.unlock()
                    }
                }
            }
        }
    }

    override suspend fun stopUdpServer() {
        udpServerJob?.cancelAndJoin()
        udpServerJob = null
        udpServerLock.unlock()
    }

    override suspend fun startCheckItemsSyncListener() {
        tcpServerLock.lock()
        withContext(Dispatchers.IO) {
            tcpServerJob = launch {
                while (true) {
                    val buffer = ByteArray(256)
                    var socket: DatagramSocket? = null
                    try {
                        socket = DatagramSocket(8888)
                        socket.broadcast = true
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        val data = String(packet.data, 0, packet.length)
                        val defconMessage = Json.decodeFromString<DefconMessage>(data)
                        if (defconMessage.uuid != base.defconStatusMessageUuid) onDefconStatusReceived(
                            defconMessage.status
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        socket?.close()
                    }
                }
            }
        }
    }

    override suspend fun stopTcpServer() {
        tcpServerJob?.cancelAndJoin()
        tcpServerJob = null
        tcpServerLock.unlock()
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
        for (weakRef in onCheckItemsReceivedListeners) {
            try {
                weakRef.get()?.onCheckItemsReceived(checkItems)
            } catch (e: Exception) {
            }
        }
    }

    override fun onDefconStatusReceived(status: Int) {
        for (weakRef in onDefconStatusReceivedListeners) {
            try {
                weakRef.get()?.onDefconStatusReceived(status)
            } catch (e: Exception) {
            }
        }
    }

    override suspend fun onRequestReceived(requestCode: Int, address: InetAddress) {
        if (requestCode == NetworkImpl.CHECKITEMSSYNC_REQUESTCODE) (base.client as Synchronizer).syncCheckItems(
            address
        )
    }
}