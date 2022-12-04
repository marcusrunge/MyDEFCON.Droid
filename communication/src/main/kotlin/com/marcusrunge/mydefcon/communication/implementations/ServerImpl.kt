package com.marcusrunge.mydefcon.communication.implementations

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnReceived
import com.marcusrunge.mydefcon.communication.interfaces.Server
import com.marcusrunge.mydefcon.communication.interfaces.Synchronizer
import com.marcusrunge.mydefcon.communication.models.DefconMessage
import com.marcusrunge.mydefcon.communication.models.RequestMessage
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.lang.ref.WeakReference
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.ServerSocket
import java.util.concurrent.locks.ReentrantLock

internal class ServerImpl(private val base: NetworkBase) : Server, OnReceived {
    private val onDefconStatusReceivedListeners: MutableList<WeakReference<OnDefconStatusReceivedListener>> =
        mutableListOf()
    private var udpServerJob: Job? = null
    private var tcpServerJob: Job? = null
    private val udpServerLock = Mutex()
    private val tcpServerLock = Mutex()

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

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun startUdpServer() {
        withContext(Dispatchers.IO) {
            udpServerLock.withLock(this) {
                udpServerJob = launch {
                    val whileLock = ReentrantLock()
                    val socket = DatagramSocket(8888)
                    socket.broadcast = true
                    try {
                        while (true) {
                            whileLock.lock()
                            val buffer = ByteArray(256)
                            val packet = DatagramPacket(buffer, buffer.size)
                            try {
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
                            } catch (_: Exception) {
                            } finally {
                                if (whileLock.isHeldByCurrentThread) {
                                    whileLock.unlock()
                                }
                            }
                        }
                    } catch (_: Exception) {
                    } finally {
                        socket.close()
                    }
                }
            }
        }
    }

    override suspend fun stopUdpServer() {
        udpServerJob?.cancelAndJoin()
        udpServerJob = null
        if (udpServerLock.isLocked)
            udpServerLock.unlock(this)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun startTcpServer() {
        withContext(Dispatchers.IO) {
            tcpServerLock.withLock(this) {
                tcpServerJob = launch {
                    val serverSocket = ServerSocket(8889)
                    val whileLock = ReentrantLock()
                    try {
                        while (true) {
                            whileLock.lock()
                            val socket = serverSocket.accept()
                            try {
                                val writer = PrintWriter(
                                    BufferedWriter(OutputStreamWriter(socket.getOutputStream())),
                                    true
                                )
                                val observer = Observer<MutableList<CheckItem>> {
                                    val json = Json.encodeToString(it)
                                    writer.println(json)
                                    writer.close()
                                }
                                val checkItems = base.data?.repository?.checkItems?.getAll()
                                checkItems?.observeForeverOnce(observer)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            } finally {
                                whileLock.unlock()
                            }
                        }
                    } catch (_: Exception) {
                    } finally {
                        serverSocket.close()
                    }
                }
            }
        }
    }

    override suspend fun stopTcpServer() {
        tcpServerJob?.cancelAndJoin()
        tcpServerJob = null
        if (tcpServerLock.isLocked)
            tcpServerLock.unlock(this)
    }

    override fun onDefconStatusReceived(status: Int) {
        for (weakRef in onDefconStatusReceivedListeners) {
            try {
                weakRef.get()?.onDefconStatusReceived(status)
            } catch (_: Exception) {
            }
        }
    }

    override suspend fun onRequestReceived(requestCode: Int, address: InetAddress) {
        if (requestCode == NetworkImpl.CHECKITEMSSYNC_REQUESTCODE) (base.client as Synchronizer).syncCheckItems(
            address
        )
    }
}

fun <T> LiveData<T>.observeForeverOnce(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}