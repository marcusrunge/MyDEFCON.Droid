package com.marcusrunge.mydefcon.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.marcusrunge.mydefcon.services.ForegroundSocketService

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive (" + intent.action.toString() + ")")
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            Log.i(TAG, "onReceive launch")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) -> {
                        if (!ForegroundSocketService.isRunning)
                            context.startForegroundService(
                                Intent(
                                    context,
                                    ForegroundSocketService::class.java
                                )
                            )
                    }
                    else -> {

                    }
                }
            } else if (!ForegroundSocketService.isRunning) {
                context.startForegroundService(Intent(context, ForegroundSocketService::class.java))
            }
        } else {
            Log.i(TAG, "onReceive other")
        }
    }
}