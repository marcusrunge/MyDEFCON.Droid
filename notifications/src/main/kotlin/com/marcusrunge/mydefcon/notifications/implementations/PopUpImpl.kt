package com.marcusrunge.mydefcon.notifications.implementations

import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import com.marcusrunge.mydefcon.notifications.interfaces.PopUp

internal class PopUpImpl(notificationsBase: NotificationsBase) : PopUp {
    companion object {
        private var popUp: PopUp? = null
        fun create(notificationsBase: NotificationsBase): PopUp = when {
            popUp != null -> popUp!!
            else -> {
                popUp = PopUpImpl(notificationsBase)
                popUp!!
            }
        }
    }
}