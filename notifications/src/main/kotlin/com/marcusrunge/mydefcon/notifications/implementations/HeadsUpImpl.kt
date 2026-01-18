package com.marcusrunge.mydefcon.notifications.implementations

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import com.marcusrunge.mydefcon.notifications.interfaces.HeadsUp

internal class HeadsUpImpl(private val notificationsBase: NotificationsBase) : HeadsUp {

    init {
        createNotificationChannel()
    }

    @SuppressLint("MissingPermission")
    override fun showBasicUrgent(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_MAX
        )
    }

    @SuppressLint("MissingPermission")
    override fun showBasicHigh(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_HIGH
        )
    }

    @SuppressLint("MissingPermission")
    override fun showBasicMedium(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_DEFAULT
        )
    }

    @SuppressLint("MissingPermission")
    override fun showBasicLow(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_LOW
        )
    }

    @SuppressLint("MissingPermission")
    override fun showExpandedUrgent(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showExpandedNotificationWithPriority(
            smallIcon,
            largeIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_MAX
        )
    }

    @SuppressLint("MissingPermission")
    override fun showExpandedHigh(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showExpandedNotificationWithPriority(
            smallIcon,
            largeIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_HIGH
        )
    }

    @SuppressLint("MissingPermission")
    override fun showExpandedMedium(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showExpandedNotificationWithPriority(
            smallIcon,
            largeIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_DEFAULT
        )
    }

    @SuppressLint("MissingPermission")
    override fun showExpandedLow(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        notificationId: Int,
        ongoing: Boolean
    ) {
        showExpandedNotificationWithPriority(
            smallIcon,
            largeIcon,
            textTitle,
            textContent,
            ongoing,
            notificationId,
            NotificationCompat.PRIORITY_LOW
        )
    }

    override fun isNotificationShown(notificationId: Int): Boolean {
        val notificationManager =
            notificationsBase.context?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        val activeNotifications = notificationManager.activeNotifications
        for (notification in activeNotifications) {
            if (notification.id == notificationId) {
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun showBasicNotificationWithPriority(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        notificationId: Int,
        priority: Int
    ) {
        clearNotificationsInChannel()
        val notification = buildBasicNotification(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            priority
        )
        NotificationManagerCompat.from(notificationsBase.context!!)
            .notify(notificationId, notification)
    }

    @SuppressLint("MissingPermission")
    private fun showExpandedNotificationWithPriority(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        notificationId: Int,
        priority: Int
    ) {
        clearNotificationsInChannel()
        val notification = buildExpandedNotification(
            smallIcon,
            largeIcon,
            textTitle,
            textContent,
            ongoing,
            priority
        )
        NotificationManagerCompat.from(notificationsBase.context!!)
            .notify(notificationId, notification)
    }

    private fun clearNotificationsInChannel() {
        val manager =
            notificationsBase.context!!.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        manager.activeNotifications?.forEach {
            if (it.notification.channelId == MYDEFCON_STATUS_CHANNEL_ID) {
                manager.cancel(it.id)
            }
        }
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
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(priority)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        if (smallIcon != null) builder.setSmallIcon(smallIcon)
        if (textTitle != null) builder.setContentTitle(textTitle)
        if (textContent != null) builder.setContentText(textContent)
        return builder.build()
    }

    private fun buildExpandedNotification(
        smallIcon: Int?,
        largeIcon: Int?,
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
            .setCategory(NotificationCompat.CATEGORY_STATUS)
            .setPriority(priority)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        if (smallIcon != null) builder.setSmallIcon(smallIcon)
        if (textTitle != null) builder.setContentTitle(textTitle)
        if (textContent != null) {
            builder.setContentText(textContent)
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(textContent))
        }

        if (largeIcon != null) {
            val largeIconBitmap =
                BitmapFactory.decodeResource(notificationsBase.context.resources, largeIcon)
            builder.setLargeIcon(largeIconBitmap)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        val name = notificationsBase.context?.getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(MYDEFCON_STATUS_CHANNEL_ID, name, importance)

        val notificationManager: NotificationManager =
            notificationsBase.context?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val MYDEFCON_STATUS_CHANNEL_ID = "mydefcon_status"

        @Volatile
        private var headsUp: HeadsUp? = null

        fun create(notificationsBase: NotificationsBase): HeadsUp =
            headsUp ?: synchronized(this) {
                headsUp ?: HeadsUpImpl(notificationsBase).also { headsUp = it }
            }
    }
}
