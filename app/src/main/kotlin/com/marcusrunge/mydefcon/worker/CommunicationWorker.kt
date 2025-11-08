package com.marcusrunge.mydefcon.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.Observer
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import com.marcusrunge.mydefcon.utils.LiveDataManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CommunicationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val core: Core,
    //private val data: Data,
    private val notifications: Notifications,
    private val lifeDataManager: LiveDataManager
) :
    CoroutineWorker(context, parameters),
    LifecycleOwner {

    private var started = false
    private var _workerDefconStatus: Int? = null
    private lateinit var lifecycleRegistry: LifecycleRegistry
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry


    override suspend fun doWork(): Result {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        if (!started) {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
            started = true
            _workerDefconStatus = core.preferences.status
        }
        showNotification()
        lifeDataManager.intent.observe(this, intentObserver)
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        return Result.success()
    }

    val intentObserver = Observer<Intent> { intent ->
        if (intent.action == "com.marcusrunge.mydefcon.DEFCONSTATUS_SELECTED") {
            val data = intent.getIntExtra("data", 5)
            //val source = intent.getStringExtra("source")
            _workerDefconStatus = data
            showNotification()
        }
    }

    private fun showNotification() {
        val smallIcon = when (_workerDefconStatus) {
            1 -> R.drawable.ic_stat1
            2 -> R.drawable.ic_stat2
            3 -> R.drawable.ic_stat3
            4 -> R.drawable.ic_stat4
            else -> R.drawable.ic_stat5
        }
        notifications.headsUp.showBasicUrgent(
            smallIcon,
            null,
            "DEFCON ${core.preferences.status}",
            true
        )
    }
}