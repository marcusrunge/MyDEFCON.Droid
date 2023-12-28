package com.marcusrunge.mydefcon.notifications.implementations

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import com.marcusrunge.mydefcon.notifications.interfaces.HeadsUp

internal class HeadsUpImpl(private val notificationsBase: NotificationsBase) : HeadsUp {
    companion object {
        private var headsUp: HeadsUp? = null
        const val MYDEFCON_STATUS_CHANNEL_ID = "mydefcon_status"
        fun create(notificationsBase: NotificationsBase): HeadsUp = when {
            headsUp != null -> headsUp!!
            else -> {
                headsUp = HeadsUpImpl(notificationsBase)
                headsUp!!
            }
        }
    }

    private var notificationId: Int = 0

    init {
        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    override fun showBasicUrgent(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        val notification = buildBasicNotification(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_MAX
        )
        with(NotificationManagerCompat.from(notificationsBase.context!!)) {
            notificationId++
            notify(notificationId, notification)
        }
    }

    @SuppressLint("MissingPermission")
    override fun showBasicHigh(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        val notification = buildBasicNotification(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_HIGH
        )
        with(NotificationManagerCompat.from(notificationsBase.context!!)) {
            notificationId++
            notify(notificationId, notification)
        }
    }

    @SuppressLint("MissingPermission")
    override fun showBasicMedium(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        val notification = buildBasicNotification(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_LOW
        )
        with(NotificationManagerCompat.from(notificationsBase.context!!)) {
            notificationId++
            notify(notificationId, notification)
        }
    }

    @SuppressLint("MissingPermission")
    override fun showBasicLow(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        val notification = buildBasicNotification(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_MIN
        )
        with(NotificationManagerCompat.from(notificationsBase.context!!)) {
            notificationId++
            notify(notificationId, notification)
        }
    }

    override fun showExpandedUrgent(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun showExpandedHigh(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun showExpandedMedium(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun showExpandedLow(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    private fun buildBasicNotification(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        priority: Int
    ): Notification {
        val pendingIntent = PendingIntent.getActivity(
            notificationsBase.context,
            0,
            notificationsBase.context?.packageManager?.getLaunchIntentForPackage(notificationsBase.context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(
            notificationsBase.context!!,
            MYDEFCON_STATUS_CHANNEL_ID
        )
            .setContentIntent(pendingIntent)
            .setOngoing(ongoing)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setPriority(priority)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        if (smallIcon != null) builder.setSmallIcon(smallIcon)
        if (textTitle != null) builder.setContentTitle(textTitle)
        if (textContent != null) builder.setContentText(textContent)
        return builder.build()
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            MYDEFCON_STATUS_CHANNEL_ID,
            notificationsBase.context?.getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager =
            notificationsBase.context?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(notificationChannel)
    }
}