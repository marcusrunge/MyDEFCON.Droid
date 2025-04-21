package com.marcusrunge.mydefcon.services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DefconFirebaseMessagingService : FirebaseMessagingService() {
    val TAG: String = "DefconFirebaseMessagingService"
    @Inject
    lateinit var firebase: Firebase
    @Inject
    lateinit var core: Core

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        core.preferences.fcmRegistrationToken = token
    }
}