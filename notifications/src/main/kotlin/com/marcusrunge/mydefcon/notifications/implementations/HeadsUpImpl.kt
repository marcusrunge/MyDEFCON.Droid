package com.marcusrunge.mydefcon.notifications.implementations

import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import com.marcusrunge.mydefcon.notifications.interfaces.HeadsUp

internal class HeadsUpImpl(notificationsBase: NotificationsBase) : HeadsUp {
    companion object {
        private var headsUp: HeadsUp? = null
        fun create(notificationsBase: NotificationsBase): HeadsUp = when {
            headsUp != null -> headsUp!!
            else -> {
                headsUp = HeadsUpImpl(notificationsBase)
                headsUp!!
            }
        }
    }

    override fun showBasicUrgent(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showBasicHigh(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showBasicMedium(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showBasicLow(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showExpandedUrgent(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showExpandedHigh(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showExpandedMedium(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }

    override fun showExpandedLow(textTitle: String, textContent: String, ongoing: Boolean) {
        TODO("Not yet implemented")
    }
}