package com.marcusrunge.mydefcon.notifications.bases

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.interfaces.HeadsUp
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import com.marcusrunge.mydefcon.notifications.interfaces.PopUp

internal abstract class NotificationsBase(internal val context: Context?, internal val core: Core?) :
    Notifications {
    protected lateinit var _headsUp: HeadsUp
    protected lateinit var _popUp: PopUp
    protected lateinit var _onInitialize: (() -> Unit)
    override val headsUp: HeadsUp
        get() = _headsUp

    override val popUp: PopUp
        get() = _popUp

    override fun initialize() {
        _onInitialize.invoke()
    }
}
