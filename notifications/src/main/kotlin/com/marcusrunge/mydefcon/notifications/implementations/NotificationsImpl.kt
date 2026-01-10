package com.marcusrunge.mydefcon.notifications.implementations

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import kotlinx.coroutines.launch

/**
 * An implementation of the notification logic for the MyDEFCON application.
 *
 * This class is responsible for observing changes in the DEFCON status and displaying
 * appropriate heads-up notifications. It initializes the notification system and
 * collects a flow of status updates.
 *
 * @param context The application context.
 * @param core The core component of the application, which provides access to preferences,
 *             live data, and coroutine scopes.
 */
internal class NotificationsImpl(context: Context?, core: Core?) :
    NotificationsBase(context, core) {
    init {
        _headsUp = HeadsUpImpl.create(this)
        _popUp = PopUpImpl.create(this)
        _onInitialize = { onInitialize() }
    }

    /**
     * Initializes the notification service.
     *
     * This function starts collecting the DEFCON status flow and shows an initial
     * notification based on the current status stored in preferences.
     */
    private fun onInitialize() {
        collectDefconStatusFlow()
        // Safely show initial notification if the status is available.
        core?.preferences?.status?.let { showNotification(it) }
    }

    /**
     * Collects the DEFCON status from the live data flow and triggers notifications.
     *
     * This function launches a coroutine to observe the `defconStatusFlow`.
     * Upon receiving a new status, it checks for notification permissions and then
     * calls `showNotification` to display or update the notification.
     */
    private fun collectDefconStatusFlow() {
        core?.coroutineScope?.launch {
            core.liveDataManager?.defconStatusFlow?.collect { pair ->
                // Only show notification if permission has been granted.
                if (core.preferences?.isPostNotificationPermissionGranted == true) {
                    showNotification(pair.first)
                }
            }
        }
    }

    /**
     * Displays a heads-up notification with the given DEFCON status.
     *
     * @param status The DEFCON status to display (1-5). The notification's icon and
     *               text are determined by this status.
     */
    private fun showNotification(status: Int) {
        val smallIcon = when (status) {
            1 -> R.drawable.ic_stat1
            2 -> R.drawable.ic_stat2
            3 -> R.drawable.ic_stat3
            4 -> R.drawable.ic_stat4
            else -> R.drawable.ic_stat5
        }

        headsUp.showBasicUrgent(
            smallIcon = smallIcon,
            textTitle = null,
            textContent = "DEFCON $status",
            ongoing = true
        )
    }
}