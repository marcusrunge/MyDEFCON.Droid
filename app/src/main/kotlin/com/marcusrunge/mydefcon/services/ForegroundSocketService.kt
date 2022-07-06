package com.marcusrunge.mydefcon.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
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
import com.marcusrunge.mydefcon.receiver.CheckItemsReceiver
import com.marcusrunge.mydefcon.receiver.DefconStatusReceiver
import com.marcusrunge.mydefcon.ui.status.StatusFragment
import com.marcusrunge.mydefcon.ui.status.StatusViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundSocketService : LifecycleService(), OnDefconStatusReceivedListener,
    OnCheckItemsReceivedListener, com.marcusrunge.mydefcon.receiver.OnDefconStatusReceivedListener {
    @Inject
    lateinit var core: Core

    @Inject
    lateinit var communication: Communication

    @Inject
    lateinit var data: Data
    private var started = false
    private var udpServerJob: Job? = null
    private var tcpServerJob: Job? = null
    private var receiver: DefconStatusReceiver = DefconStatusReceiver()

    override fun onBind(intent: Intent): IBinder? {
        return null
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
        isRunning = true
        receiver.setOnDefconStatusReceivedListener(this)
        return START_STICKY
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
        isRunning = false
        receiver.removeOnDefconStatusReceivedListener()
        unregisterReceiver(receiver)
    }


    override fun onDefconStatusReceived(status: Int) {
        core.preferences.status = status
        onReceived(status, null)
        showNotification()
    }

    override fun onDefconStatusReceived(status: Int, source: String?) {
        if (source == StatusFragment::class.java.canonicalName) {
            showNotification()
        }
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
        val notificationChannel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
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
        if (status != null) Intent(this, DefconStatusReceiver::class.java).also { intent ->
            intent.action = "com.marcusrunge.mydefcon.DEFCONSTATUS_RECEIVED"
            intent.putExtra("data", status)
            intent.putExtra("source", ForegroundSocketService::class.java.canonicalName)
            sendBroadcast(intent)
        }
        if (items != null) if (status != null) Intent(this, CheckItemsReceiver::class.java).also { intent ->
            intent.action = "com.marcusrunge.mydefcon.CHECKITEMS_RECEIVED"
            intent.putExtra("data", items as Serializable)
            sendBroadcast(intent)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 12345
        private const val NOTIFICATION_CHANNEL_ID = "socket_listening"
        var isRunning = false
    }
}