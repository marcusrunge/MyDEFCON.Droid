package com.marcusrunge.mydefcon.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.communication.interfaces.OnCheckItemsReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundSocketService : LifecycleService(), OnDefconStatusReceivedListener,
    OnCheckItemsReceivedListener {
    @Inject
    lateinit var core: Core

    @Inject
    lateinit var communication: Communication

    @Inject
    lateinit var data: Data

    private var started = false
    private val localBinder = LocalBinder()
    private var udpServerJob: Job? = null
    private var tcpServerJob: Job? = null
    private val onReceivedListeners: MutableList<WeakReference<OnReceivedListener>> =
        mutableListOf()

    fun addOnReceivedListener(listener: OnReceivedListener) {
        onReceivedListeners.add(WeakReference(listener))
    }

    fun removeOnReceivedListener(listener: OnReceivedListener) {
        val iterator: MutableIterator<WeakReference<OnReceivedListener>> =
            onReceivedListeners.iterator()
        while (iterator.hasNext()) {
            val weakRef: WeakReference<OnReceivedListener> = iterator.next()
            if (weakRef.get() === listener) {
                iterator.remove()
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!started) {
            started = true
            udpServerJob = lifecycleScope.launch {
                communication.network.server.startUdpServer()
            }
            tcpServerJob = lifecycleScope.launch {
                communication.network.server.startTcpServer()
            }
            communication.network.server.addOnDefconStatusReceivedListener(this)
            communication.network.client.addOnCheckItemsReceivedListener(this)
        }
        showNotification()
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        handleBind()
        return localBinder
    }

    override fun onRebind(intent: Intent?) {
        handleBind()
    }

    private fun handleBind() {
        startService(Intent(this, this::class.java))
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleScope.launch {
            communication.network.server.stopUdpServer()
        }.invokeOnCompletion {
            udpServerJob?.cancel()
        }
        lifecycleScope.launch {
            communication.network.server.stopTcpServer()
        }.invokeOnCompletion {
            tcpServerJob?.cancel()
        }
    }

    override fun onDefconStatusReceived(status: Int) {
        core.preferences.status = status
        onReceived(status, null)
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
        val checkItemsObserver = Observer<MutableList<CheckItem>> { it0 ->
            checkItems.forEach { it1 ->
                var found = false
                it0.forEach { it3 ->
                    if (it1.uuid == it3.uuid) {
                        found = true
                        it1.id = it3.id
                        data.repository.checkItems.update(it1)
                    }
                }
                if (!found) {
                    it1.id = 0
                    data.repository.checkItems.insert(it1)
                }
            }
        }
        val observableCheckItems = data.repository.checkItems.getAll()
        observableCheckItems.observe(this, checkItemsObserver)
        onReceived(null, checkItems)
    }

    private fun showNotification() {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(notificationChannel)
        }
    }

    private fun buildNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(this.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val smallIcon = when (core.preferences.status) {
            1 -> R.drawable.ic_stat1
            2 -> R.drawable.ic_stat2
            3 -> R.drawable.ic_stat3
            4 -> R.drawable.ic_stat4
            else -> R.drawable.ic_stat5
        }

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentText("DEFCON ${core.preferences.status}")
            .setContentIntent(pendingIntent)
            .setSmallIcon(smallIcon)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
    }

    private fun onReceived(status: Int?, items: List<CheckItem>?) {
        for (weakRef in onReceivedListeners) {
            try {
                if (status != null) weakRef.get()?.onDefconStatusReceived(status)
                if (items != null) weakRef.get()?.onCheckItemsReceived(items)
            } catch (e: Exception) {
            }
        }
    }

    private companion object {
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "SocketListening"
    }

    internal inner class LocalBinder : Binder() {
        fun getService(): ForegroundSocketService = this@ForegroundSocketService
    }
}

class ForegroundSocketServiceConnection @Inject constructor() : ServiceConnection {

    var service: ForegroundSocketService? = null
        private set

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as ForegroundSocketService.LocalBinder).getService()
    }

    override fun onServiceDisconnected(name: ComponentName) {
        service = null
    }
}

interface OnReceivedListener {
    /**
     * Occurs when check items have been received.
     * @param checkItems the check items.
     */
    fun onCheckItemsReceived(checkItems: List<CheckItem>)

    /**
     * Occurs when a new defcon status has been issued.
     * @param status the defcon status
     */
    fun onDefconStatusReceived(status: Int)
}