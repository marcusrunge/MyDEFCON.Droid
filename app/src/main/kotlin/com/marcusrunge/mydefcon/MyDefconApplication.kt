package com.marcusrunge.mydefcon

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.marcusrunge.mydefcon.core.interfaces.Core
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyDefconApplication : Application(), Configuration.Provider {
    val TAG: String = "MyDefconApplication"
    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    @Inject
    lateinit var core: Core
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg ="FCM Registration token: $token"
            Log.d(TAG, msg)
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            core.preferences?.fcmRegistrationToken = token
        })
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().setWorkerFactory(workerFactory).build()
}