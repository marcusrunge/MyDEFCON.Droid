package com.marcusrunge.mydefcon.notifications.implementations

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.bases.NotificationsBase
import kotlinx.coroutines.launch

internal class NotificationsImpl(context: Context?, core: Core?) :
    NotificationsBase(context, core) {
    init {
        _headsUp = HeadsUpImpl.create(this)
        _popUp = PopUpImpl.create(this)
        _onInitialize = { onInitialize() }
        //collectDefconStatusFlow()
    }

    private fun onInitialize() {
        collectDefconStatusFlow()
        showNotification(core?.preferences?.status!!)
    }

    private fun collectDefconStatusFlow() {
        core?.coroutineScope?.launch {
            core.liveDataManager?.defconStatusFlow?.collect { pair ->
                if(core.preferences?.isPostNotificationPermissionGranted!!)
                    showNotification(pair.first)
            }
        }
    }

    private fun showNotification(status: Int) {
        val smallIcon = when (status) {
            1 -> R.drawable.ic_stat1
            2 -> R.drawable.ic_stat2
            3 -> R.drawable.ic_stat3
            4 -> R.drawable.ic_stat4
            else -> R.drawable.ic_stat5
        }
        headsUp.showBasicUrgent(
            smallIcon,
            null,
            "DEFCON ${core?.preferences?.status}",
            true
        )
    }
}