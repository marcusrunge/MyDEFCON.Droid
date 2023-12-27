package com.marcusrunge.mydefcon.notifications.implementations

import android.content.Context
import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase

internal class NotificationsImpl(context: Context?) : NotificationsBase(context) {
    init {
        _headsUp = HeadsUpImpl.create(this)
        _popUp = PopUpImpl.create(this)
    }
}