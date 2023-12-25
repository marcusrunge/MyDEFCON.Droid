package com.marcusrunge.mydefcon.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.communication.interfaces.OnCheckItemsReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
public class CommunicationWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters), OnDefconStatusReceivedListener,
    OnCheckItemsReceivedListener, com.marcusrunge.mydefcon.receiver.OnDefconStatusReceivedListener{
    @Inject
    lateinit var core: Core

    @Inject
    lateinit var communication: Communication

    @Inject
    lateinit var data: Data
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

    override fun onDefconStatusReceived(status: Int, source: String?) {
        TODO("Not yet implemented")
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
        TODO("Not yet implemented")
    }

    override fun onDefconStatusReceived(status: Int) {
        TODO("Not yet implemented")
    }
}