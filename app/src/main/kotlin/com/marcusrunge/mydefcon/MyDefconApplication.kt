package com.marcusrunge.mydefcon

import android.app.Application
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyDefconApplication : Application() {
    val TAG: String = "MyDefconApplication"
    @Inject
    lateinit var core: Core
    @Inject
    lateinit var firebase: Firebase
    @Inject
    lateinit var notifications: Notifications
    override fun onCreate() {
        super.onCreate()
        notifications.initialize()
    }
}