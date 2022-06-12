package com.marcusrunge.mydefcon.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.marcusrunge.mydefcon.BuildConfig
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.core.interfaces.Core
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundSocketService : LifecycleService() {
    @Inject
    lateinit var core: Core
    @Inject
    lateinit var communication: Communication

    private var started = false
    private var isForeground = false
    private val localBinder = LocalBinder()
    private var bindCount = 0
    private fun isBound() = bindCount > 0

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //if (intent?.action == ACTION_STOP_UPDATES) { }
        if (!started) {
            started = true
            lifecycleScope.launch {
                //TODO:Start UDP Server
            }
            lifecycleScope.launch {
                //TODO:Start TCP Server
            }
        }
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
        bindCount++
        startService(Intent(this, this::class.java))
    }

    override fun onUnbind(intent: Intent?): Boolean {
        bindCount--
        lifecycleScope.launch {
            delay(UNBIND_DELAY_MILLIS)
            manageLifetime()
        }
        return true
    }

    private fun manageLifetime() {
        when {
            isBound() -> exitForeground()
            core.preferences.status>0 -> enterForeground()
            else -> stopSelf()
        }
    }

    private fun exitForeground() {
        if (isForeground) {
            isForeground = false
            stopForeground(true)
        }
    }

    private fun enterForeground() {
        if (!isForeground) {
            isForeground = true
            showNotification()
        }
    }

    private fun showNotification() {
        if (!isForeground) {
            return
        }
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

    private fun buildNotification() : Notification {
        // Tapping the notification opens the app.
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            packageManager.getLaunchIntentForPackage(this.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Include an action to stop location updates without going through the app UI.
        val stopIntent = PendingIntent.getService(
            this,
            0,
            Intent(this, this::class.java).setAction(ACTION_STOP_UPDATES),
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
            .setContentTitle(getString(R.string.notification_title))
            .setContentText("DEFCON ${core.preferences.status}")
            .setContentIntent(pendingIntent)
            .setSmallIcon(smallIcon)
             //.addAction(R.drawable.ic_stop, getString(R.string.stop), stopIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()
    }

    private companion object {
        const val UNBIND_DELAY_MILLIS = 2000.toLong() // 2 seconds
        const val NOTIFICATION_ID = 1
        const val NOTIFICATION_CHANNEL_ID = "SocketListening"
        const val ACTION_STOP_UPDATES = BuildConfig.APPLICATION_ID + ".ACTION_STOP_UPDATES"
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