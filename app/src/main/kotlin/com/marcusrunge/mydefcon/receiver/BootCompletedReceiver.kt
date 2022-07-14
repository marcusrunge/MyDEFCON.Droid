package com.marcusrunge.mydefcon.receiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.marcusrunge.mydefcon.services.ForegroundSocketService

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action || Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE == intent.action) {
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
        }
    }
}