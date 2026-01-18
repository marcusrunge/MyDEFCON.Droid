package com.marcusrunge.mydefcon.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * A [CoroutineWorker] for handling background tasks related to DEFCON status updates.
 *
 * This worker is responsible for periodically checking the DEFCON status. If the user
 * has not joined a DEFCON group, it emits the locally stored status to trigger a
 * notification. If the user has joined a group, it fetches the latest status from
 * Firebase Realtime Database.
 *
 * @param appContext The application context.
 * @param params Parameters for the worker.
 * @param firebase The Firebase component for database interactions.
 * @param core The core component providing access to preferences and live data.
 * @param notifications The notifications component for displaying alerts.
 */
@HiltWorker
class DefconWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val firebase: Firebase,
    private val core: Core,
    private val notifications: Notifications
) : CoroutineWorker(
    appContext,
    params
) {
    /**
     * The main entry point for the worker's execution.
     *
     * This function determines whether to check for a local status change or fetch the
     * status from a remote source based on whether a DEFCON group has been joined.
     *
     * @return [Result.success] if the work finished successfully, [Result.failure] otherwise.
     */
    override suspend fun doWork(): Result {
        try {
            // Check if the user has joined a DEFCON group.
            if (core.preferences?.joinedDefconGroupId.isNullOrEmpty()) {
                // If not in a group, check the local status.
                // Only show a notification if one for the current status isn't already visible.
                if (!notifications.headsUp.isNotificationShown(1000 + core.preferences?.status!!)) {
                    // Emit the local status to trigger observers (e.g., to show a notification).
                    core.liveDataManager?.emitDefconStatus(core.preferences?.status!!, this.javaClass)
                }
            } else {
                // If in a group, fetch the latest DEFCON status from Firebase Realtime Database.
                firebase.realtime.fetchDefconStatus(core.preferences?.joinedDefconGroupId!!)
            }
            // Indicate that the work finished successfully.
            return Result.success()
        } catch (e: Exception) {
            // Log any errors that occur during the process.
            Log.e("DefconWorker", "Error during work", e)
            // Indicate that the work failed.
            return Result.failure()
        }
    }
}
