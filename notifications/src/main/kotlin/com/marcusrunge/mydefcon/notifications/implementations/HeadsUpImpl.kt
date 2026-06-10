package com.marcusrunge.mydefcon.notifications.implementations

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import com.marcusrunge.mydefcon.notifications.interfaces.HeadsUp

/**
 * Implementation of the [HeadsUp] interface for managing heads-up notifications.
 * This class handles the creation, display, and management of status-related notifications
 * with different priority levels and styles.
 *
 * @param notificationsBase The base notifications component providing context and shared resources.
 */
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
        val context = notificationsBase.context ?: return false
        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        // Check if a notification with the given ID is currently active in our specific channel
        return notificationManager.activeNotifications.any {
            it.id == notificationId && it.notification.channelId == MYDEFCON_STATUS_CHANNEL_ID
        }
    }

    /**
     * Clears all notifications in the [MYDEFCON_STATUS_CHANNEL_ID] except for the one being shown.
     * This ensures only one status notification is displayed at a time.
     */
    private fun clearNotificationsInChannelExcept(notificationId: Int) {
        val context = notificationsBase.context ?: return
        val manager = context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

        manager.activeNotifications.forEach {
            if (it.id != notificationId && it.notification.channelId == MYDEFCON_STATUS_CHANNEL_ID) {
                manager.cancel(it.id)
            }
        }
    }

    /**
     * Shared logic for displaying basic style notifications.
     */
    @SuppressLint("MissingPermission")
    private fun showBasicNotificationWithPriority(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        notificationId: Int,
        priority: Int
    ) {
        val context = notificationsBase.context ?: return
        if (isNotificationShown(notificationId)) return

        clearNotificationsInChannelExcept(notificationId)
        val notification = buildBasicNotification(
            context,
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            priority
        )
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    /**
     * Shared logic for displaying expanded style notifications.
     */
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
        val context = notificationsBase.context ?: return
        if (isNotificationShown(notificationId)) return

        clearNotificationsInChannelExcept(notificationId)
        val notification = buildExpandedNotification(
            context,
            smallIcon,
            largeIcon,
            textTitle,
            textContent,
            ongoing,
            priority
        )
        NotificationManagerCompat.from(context).notify(notificationId, notification)
    }

    /**
     * Configures the common properties of the notification builder.
     */
    private fun NotificationCompat.Builder.configureCommon(
        pendingIntent: PendingIntent,
        ongoing: Boolean,
        priority: Int,
        smallIcon: Int?,
        textTitle: String?
    ): NotificationCompat.Builder = apply {
        setOnlyAlertOnce(true)
        setContentIntent(pendingIntent)
        setOngoing(ongoing)
        setCategory(NotificationCompat.CATEGORY_STATUS)
        setPriority(priority)
        setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        smallIcon?.let { setSmallIcon(it) }
        textTitle?.let { setContentTitle(it) }
    }

    private fun buildBasicNotification(
        context: Context,
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        priority: Int
    ): Notification {
        val pendingIntent = getLaunchPendingIntent(context)
        val builder = NotificationCompat.Builder(context, MYDEFCON_STATUS_CHANNEL_ID)
            .configureCommon(pendingIntent, ongoing, priority, smallIcon, textTitle)

        textContent?.let { builder.setContentText(it) }

        return builder.build()
    }

    private fun buildExpandedNotification(
        context: Context,
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        priority: Int
    ): Notification {
        val pendingIntent = getLaunchPendingIntent(context)
        val builder = NotificationCompat.Builder(context, MYDEFCON_STATUS_CHANNEL_ID)
            .configureCommon(pendingIntent, ongoing, priority, smallIcon, textTitle)

        textContent?.let {
            builder.setContentText(it)
            builder.setStyle(NotificationCompat.BigTextStyle().bigText(it))
        }

        largeIcon?.let {
            val bitmap = BitmapFactory.decodeResource(context.resources, it)
            builder.setLargeIcon(bitmap)
        }

        return builder.build()
    }

    private fun getLaunchPendingIntent(context: Context): PendingIntent {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        return PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    /**
     * Initializes the notification channel required for Android O and above.
     */
    private fun createNotificationChannel() {
        val context = notificationsBase.context ?: return
        val name = context.getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(MYDEFCON_STATUS_CHANNEL_ID, name, importance)

        val notificationManager =
            context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val MYDEFCON_STATUS_CHANNEL_ID = "mydefcon_status"

        @Volatile
        private var headsUp: HeadsUp? = null

        /**
         * Singleton-like factory method for [HeadsUp].
         */
        fun create(notificationsBase: NotificationsBase): HeadsUp =
            headsUp ?: synchronized(this) {
                headsUp ?: HeadsUpImpl(notificationsBase).also { headsUp = it }
            }
    }
}
