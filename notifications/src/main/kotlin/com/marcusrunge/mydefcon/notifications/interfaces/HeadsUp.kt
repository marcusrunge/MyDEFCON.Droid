package com.marcusrunge.mydefcon.notifications.interfaces

import androidx.annotation.DrawableRes

/**
 * An interface for managing and displaying heads-up notifications.
 *
 * Heads-up notifications are a special type of notification that appears as a floating
 * window at the top of the screen, allowing the user to interact with it without
 * leaving the current app. This interface provides methods to show notifications
 * with different priority levels and styles.
 */
interface HeadsUp {
    /**
     * Displays a basic heads-up notification with urgent priority.
     *
     * These notifications are for time-critical alerts that demand immediate user attention.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showBasicUrgent(@DrawableRes smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)

    /**
     * Displays a basic heads-up notification with high priority.
     *
     * Suitable for important communications, such as messages or chat events.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showBasicHigh(@DrawableRes smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)

    /**
     * Displays a basic heads-up notification with medium priority.
     *
     * For less urgent information that may be of interest to the user.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showBasicMedium(@DrawableRes smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)

    /**
     * Displays a basic heads-up notification with low priority.
     *
     * For background information or public/ambient notifications. The notification might not
     * appear as a heads-up notification on some Android versions.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showBasicLow(@DrawableRes smallIcon: Int?, textTitle: String?, textContent: String?, ongoing: Boolean)

    /**
     * Displays an expanded heads-up notification with urgent priority.
     *
     * This style allows for a larger icon and is intended for time-critical alerts
     * that demand immediate user attention.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param largeIcon A drawable resource ID for the notification's large icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showExpandedUrgent(
        @DrawableRes smallIcon: Int?,
        @DrawableRes largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )

    /**
     * Displays an expanded heads-up notification with high priority.
     *
     * Suitable for important communications that can benefit from a larger visual representation.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param largeIcon A drawable resource ID for the notification's large icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showExpandedHigh(
        @DrawableRes smallIcon: Int?,
        @DrawableRes largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )

    /**
     * Displays an expanded heads-up notification with medium priority.
     *
     * For less urgent information that may still benefit from a larger icon.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param largeIcon A drawable resource ID for the notification's large icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showExpandedMedium(
        @DrawableRes smallIcon: Int?,
        @DrawableRes largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )

    /**
     * Displays an expanded heads-up notification with low priority.
     *
     * For background information where a large icon might still be relevant. The notification
     * might not appear as a heads-up notification on some Android versions.
     *
     * @param smallIcon A drawable resource ID for the notification's small icon.
     * @param largeIcon A drawable resource ID for the notification's large icon.
     * @param textTitle The title of the notification.
     * @param textContent The main content text of the notification.
     * @param ongoing If true, the notification is persistent and cannot be dismissed by the user.
     */
    fun showExpandedLow(
        @DrawableRes smallIcon: Int?,
        @DrawableRes largeIcon: Int?,
        textTitle: String?,
        textContent: String?,
        ongoing: Boolean
    )
}