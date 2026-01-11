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
    @Inject
    lateinit var core: Core

    @Inject
    lateinit var firebase: Firebase

    @Inject
    lateinit var notifications: Notifications

    /**
     * Called when the application is starting, before any other application objects have been created.
     *
     * Use this method to perform initializations that must happen before the UI is displayed,
     * such as initializing notifications and verifying the integrity of stored DEFCON group data.
     */
    override fun onCreate() {
        super.onCreate()
        // Initialize the notification channels.
        notifications.initialize()

        // Check for stored preferences and validate them.
        core.preferences?.let { preferences ->
            // Check if the user has joined a DEFCON group.
            if (preferences.joinedDefconGroupId.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val exists = firebase.firestore.checkIfDefconGroupExists(preferences.joinedDefconGroupId)
                    // If the group does not exist, clear the stored group ID.
                    if (!exists) {
                        preferences.joinedDefconGroupId = ""
                    } else if (!firebase.firestore.checkIfFollowerInDefconGroupExists( // Check if the user is a follower of the group.
                            preferences.joinedDefconGroupId,
                            FirebaseInstallations.getInstance().id.await()
                        )
                    ) {
                        // If the user is not a follower, clear the stored group ID.
                        preferences.joinedDefconGroupId = ""
                    }
                    // If the user is a follower, fetch the DEFCON status.
                    if(exists){
                        firebase.realtime.fetchDefconStatus(preferences.joinedDefconGroupId)
                    }
                }
            }
            // Check if the user has created a DEFCON group.
            if (preferences.createdDefconGroupId.isNotEmpty()) {
                CoroutineScope(Dispatchers.IO).launch {
                    val exists = firebase.firestore.checkIfDefconGroupExists(preferences.createdDefconGroupId)
                    // If the group does not exist, clear the stored group ID.
                    if (!exists) {
                        preferences.createdDefconGroupId = ""
                    }
                }
            }
        }
    }
}