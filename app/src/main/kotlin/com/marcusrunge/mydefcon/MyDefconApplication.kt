package com.marcusrunge.mydefcon

import android.app.Application
import com.google.firebase.installations.FirebaseInstallations
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltAndroidApp
class MyDefconApplication : Application() {
    val tag: String = "MyDefconApplication"

    @Inject
    lateinit var core: Core

    @Inject
    lateinit var firebase: Firebase

    @Inject
    lateinit var notifications: Notifications
    override fun onCreate() {
        super.onCreate()
        notifications.initialize()
        if (core.preferences != null) {
            if (core.preferences!!.joinedDefconGroupId.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val exists = firebase.firestore.checkIfDefconGroupExists(core.preferences!!.joinedDefconGroupId)
                    if(!exists) {
                        core.preferences!!.joinedDefconGroupId = ""
                    }
                    else if(!firebase.firestore.checkIfFollowerInDefconGroupExists(core.preferences!!.joinedDefconGroupId, FirebaseInstallations.getInstance().id.await())){
                        core.preferences!!.joinedDefconGroupId = ""
                    }
                }
            }
            if (core.preferences!!.createdDefconGroupId.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val exists = firebase.firestore.checkIfDefconGroupExists(core.preferences!!.createdDefconGroupId)
                    if(!exists) {
                        core.preferences!!.createdDefconGroupId = ""
                    }
                }
            }
        }
    }
}