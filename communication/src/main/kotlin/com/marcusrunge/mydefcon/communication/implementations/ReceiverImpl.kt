package com.marcusrunge.mydefcon.communication.implementations

import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.OnCheckItemsReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnReceived
import com.marcusrunge.mydefcon.communication.interfaces.Receiver
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.util.concurrent.locks.ReentrantLock

internal class ReceiverImpl(private val base: NetworkBase) : Receiver, OnReceived {
    private val onDefconStatusReceivedListeners: MutableList<WeakReference<OnDefconStatusReceivedListener>> =
        mutableListOf()
    private val onCheckItemsReceivedListeners: MutableList<WeakReference<OnCheckItemsReceivedListener>> =
        mutableListOf()
    private var defconStatusChangeJob: Job? = null
    private val defconStatusChangeListenerLock = ReentrantLock()

    internal companion object {
        private var instance: Receiver? = null
        fun create(base: NetworkBase): Receiver = when {
            instance != null -> instance!!
            else -> {
                instance = ReceiverImpl(base)
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

    override suspend fun startDefconStatusChangeListener() {
        defconStatusChangeListenerLock.lock()
        withContext(Dispatchers.IO) {
            defconStatusChangeJob = launch {
                while (true) {
                    val buffer = ByteArray(256)
                    var socket: DatagramSocket? = null
                    try {
                        socket = DatagramSocket(4445)
                        socket.broadcast = true
                        val packet = DatagramPacket(buffer, buffer.size)
                        socket.receive(packet)
                        val data = String(packet.data,0,packet.length)
                        val defconMessage= Json.decodeFromString<DefconMessage>(data)
                        if(defconMessage.uuid!=base.defconStatusMessageUuid) onDefconStatusReceived(defconMessage.status)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    } finally {
                        socket?.close()
                    }
                }
            }
        }
    }

    override suspend fun stopDefconStatusChangeListener() {
        defconStatusChangeJob?.cancelAndJoin()
        defconStatusChangeJob = null
        defconStatusChangeListenerLock.unlock()
    }

    override suspend fun startCheckItemsSyncListener() {
        TODO("Not yet implemented")
    }

    override suspend fun stopCheckItemsSyncListener() {
        TODO("Not yet implemented")
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
}