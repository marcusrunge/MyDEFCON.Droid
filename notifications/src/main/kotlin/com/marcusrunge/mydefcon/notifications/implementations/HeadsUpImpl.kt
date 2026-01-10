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

/**
 * An implementation of the [HeadsUp] interface for displaying heads-up notifications.
 *
 * This class is responsible for creating and managing heads-up notifications with different priority levels.
 * It follows a singleton pattern to ensure a single instance manages all heads-up notifications.
 *
 * @param notificationsBase The base class for notifications, providing context and other dependencies.
 *                          It is crucial that the context provided by [NotificationsBase] is not null.
 */
internal class HeadsUpImpl(private val notificationsBase: NotificationsBase) : HeadsUp {

    init {
        createNotificationChannel()
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("MissingPermission")
    override fun showBasicUrgent(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_MAX
        )
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("MissingPermission")
    override fun showBasicHigh(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_HIGH
        )
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("MissingPermission")
    override fun showBasicMedium(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_DEFAULT
        )
    }

    /**
     * {@inheritDoc}
     */
    @SuppressLint("MissingPermission")
    override fun showBasicLow(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        showBasicNotificationWithPriority(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            NotificationCompat.PRIORITY_LOW
        )
    }

    /**
     * {@inheritDoc}
     *
     * TODO: Not yet implemented
     */
    override fun showExpandedUrgent(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    /**
     * {@inheritDoc}
     *
     * TODO: Not yet implemented
     */
    override fun showExpandedHigh(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    /**
     * {@inheritDoc}
     *
     * TODO: Not yet implemented
     */
    override fun showExpandedMedium(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    /**
     * {@inheritDoc}
     *
     * TODO: Not yet implemented
     */
    override fun showExpandedLow(
        smallIcon: Int?,
        largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    ) {
        TODO("Not yet implemented")
    }

    /**
     * Helper function to build and show a basic notification with a given priority.
     *
     * @param smallIcon The resource ID of the small icon for the notification.
     * @param textTitle The title of the notification.
     * @param textContent The content text of the notification.
     * @param ongoing Whether the notification should be ongoing.
     * @param priority The priority of the notification (e.g., [NotificationCompat.PRIORITY_HIGH]).
     */
    @SuppressLint("MissingPermission")
    private fun showBasicNotificationWithPriority(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        priority: Int
    ) {
        val notification = buildBasicNotification(
            smallIcon,
            textTitle,
            textContent,
            ongoing,
            priority
        )
        NotificationManagerCompat.from(notificationsBase.context!!)
            .notify(HEADS_UP_NOTIFICATION_ID, notification)
    }

    /**
     * Builds a basic notification with the provided parameters.
     *
     * @param smallIcon The resource ID of the small icon.
     * @param textTitle The title text.
     * @param textContent The content text.
     * @param ongoing `true` if the notification is ongoing, `false` otherwise.
     * @param priority The notification priority.
     * @return The constructed [Notification].
     */
    private fun buildBasicNotification(
        smallIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean,
        priority: Int
    ): Notification {
        // Intent to launch the application's main activity when the notification is tapped.
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

    /**
     * Creates the notification channel required for heads-up notifications on Android 8.0 (API 26) and higher.
     * This method should be called once during the application's startup sequence.
     * For heads-up notifications to work, the channel importance must be set to HIGH or MAX.
     */
    private fun createNotificationChannel() {
        val name = notificationsBase.context?.getString(R.string.notification_channel_name)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(MYDEFCON_STATUS_CHANNEL_ID, name, importance)

        val notificationManager: NotificationManager =
            notificationsBase.context?.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        /**
         * The ID for the heads-up notification. Using a constant ID allows for updating the same notification.
         */
        private const val HEADS_UP_NOTIFICATION_ID = 1001

        /**
         * The unique ID for the notification channel.
         */
        const val MYDEFCON_STATUS_CHANNEL_ID = "mydefcon_status"

        @Volatile
        private var headsUp: HeadsUp? = null

        /**
         * Creates or retrieves the singleton instance of the [HeadsUp] interface.
         *
         * This function uses a thread-safe, double-checked locking mechanism to ensure that only one instance
         * of [HeadsUpImpl] is created. If an instance already exists, it is returned; otherwise, a new instance
         * is created and stored.
         *
         * @param notificationsBase The base class for notifications, required for creating the instance.
         * @return The singleton [HeadsUp] instance.
         */
        fun create(notificationsBase: NotificationsBase): HeadsUp =
            headsUp ?: synchronized(this) {
                headsUp ?: HeadsUpImpl(notificationsBase).also { headsUp = it }
            }
    }
}