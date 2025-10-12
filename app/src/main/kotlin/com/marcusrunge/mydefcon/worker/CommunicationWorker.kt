package com.marcusrunge.mydefcon.worker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.hilt.work.HiltWorker
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.communication.interfaces.OnCheckItemsReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.notifications.R
import com.marcusrunge.mydefcon.notifications.interfaces.Notifications
import com.marcusrunge.mydefcon.receiver.CheckItemsReceiver
import com.marcusrunge.mydefcon.receiver.DefconStatusReceiver
import com.marcusrunge.mydefcon.ui.status.StatusFragment
import com.marcusrunge.mydefcon.utils.LiveDataManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.Serializable

@HiltWorker
class CommunicationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val core: Core,
    private val communication: Communication,
    private val data: Data,
    private val notifications: Notifications,
    private val lifeDataManager: LiveDataManager
) :
    CoroutineWorker(context, parameters), OnDefconStatusReceivedListener,
    OnCheckItemsReceivedListener, com.marcusrunge.mydefcon.receiver.OnDefconStatusReceivedListener {

    private var started = false
    private var udpServerJob: Job? = null
    private var tcpServerJob: Job? = null
    private var receiver: DefconStatusReceiver = DefconStatusReceiver()
    private var _workerDefconStatus: Int? = null
    override suspend fun doWork(): Result {
        if (!started) {
            started = true
            _workerDefconStatus = core.preferences.status
            udpServerJob = CoroutineScope(Dispatchers.IO).launch {
                communication.network.server.startUdpServer()
            }
            tcpServerJob = CoroutineScope(Dispatchers.IO).launch {
                communication.network.server.startTcpServer()
            }
            communication.network.server.addOnDefconStatusReceivedListener(this)
            communication.network.client.addOnCheckItemsReceivedListener(this)
        }
        showNotification()
        receiver.setOnDefconStatusReceivedListener(this)
        val filter = IntentFilter("com.marcusrunge.mydefcon.DEFCONSTATUS_SELECTED")
        //LocalBroadcastManager.getInstance(applicationContext).registerReceiver(receiver, filter)
        return Result.success()
    }

    override fun onDefconStatusReceived(status: Int, source: String?) {
        if (source == StatusFragment::class.java.canonicalName && _workerDefconStatus != status) {
            _workerDefconStatus = status
            showNotification()
        }
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
        val checkItemsObserver = Observer<MutableList<CheckItem>> { it0 ->
            checkItems.forEach { it1 ->
                var found = false
                it0.forEach { it3 ->
                    if (it1.uuid == it3.uuid) {
                        found = true
                        it1.id = it3.id
                        data.repository.checkItems.update(it1)
                    }
                }
                if (!found) {
                    it1.id = 0
                    data.repository.checkItems.insert(it1)
                }
            }
        }
        val observableCheckItems = data.repository.checkItems.getAllMutableLive()
        observableCheckItems.observe(ProcessLifecycleOwner.get(), checkItemsObserver)
        onReceived(null, checkItems)
    }

    override fun onDefconStatusReceived(status: Int) {
        if (_workerDefconStatus != status) {
            _workerDefconStatus = status
            core.preferences.status = status
            onReceived(status, null)
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

    private fun onReceived(status: Int?, items: List<CheckItem>?) {
        if (status != null) Intent(
            applicationContext,
            DefconStatusReceiver::class.java
        ).also { intent ->
            intent.action = "com.marcusrunge.mydefcon.DEFCONSTATUS_RECEIVED"
            intent.putExtra("data", status)
            intent.putExtra("source", CommunicationWorker::class.java.canonicalName)
            //Send to StatusViewModel
            //LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
        if (items != null) if (status != null) Intent(
            applicationContext,
            CheckItemsReceiver::class.java
        ).also { intent ->
            intent.action = "com.marcusrunge.mydefcon.CHECKITEMS_RECEIVED"
            intent.putExtra("data", items as Serializable)
            intent.putExtra("source", CommunicationWorker::class.java.canonicalName)
            //LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
        }
    }
}