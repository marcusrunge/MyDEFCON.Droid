package com.marcusrunge.mydefcon.notifications

import android.content.Context
import com.marcusrunge.mydefcon.notifications.implementations.NotificationsImpl
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications

interface NotificationsFactory {
    /**
     * Creates the core instance
     * @see Notifications
     */
    fun create(context: Context?): Notifications
}

class NotificationsFactoryImpl {
    companion object : NotificationsFactory {
        private var notifications: Notifications? = null
        override fun create(context: Context?): Notifications = when {
            notifications != null -> notifications!!
            else -> {
                notifications = NotificationsImpl(context)
                notifications!!
            }
        }
    }
}