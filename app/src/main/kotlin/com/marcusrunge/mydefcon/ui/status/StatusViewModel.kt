package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.content.Intent
import android.content.IntentFilter
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.receiver.DefconStatusReceiver
import com.marcusrunge.mydefcon.receiver.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.LiveDataManager
import com.marcusrunge.mydefcon.worker.CommunicationWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    private val app: Application, val core: Core, val communication: Communication,  val lifeDataManager: LiveDataManager
) : ObservableViewModel(app), DefaultLifecycleObserver, OnDefconStatusReceivedListener {
    private lateinit var statusViewModelOwner: LifecycleOwner
    private val isDefcon1ButtonCheckedObserver =
        Observer<Boolean> { if (it) distributeDefconStatus(1) }
    private val isDefcon2ButtonCheckedObserver =
        Observer<Boolean> { if (it) distributeDefconStatus(2) }
    private val isDefcon3ButtonCheckedObserver =
        Observer<Boolean> { if (it) distributeDefconStatus(3) }
    private val isDefcon4ButtonCheckedObserver =
        Observer<Boolean> { if (it) distributeDefconStatus(4) }
    private val isDefcon5ButtonCheckedObserver =
        Observer<Boolean> { if (it) distributeDefconStatus(5) }
    private val _isDefcon1ButtonChecked = MutableLiveData<Boolean>()
    private val _isDefcon2ButtonChecked = MutableLiveData<Boolean>()
    private val _isDefcon3ButtonChecked = MutableLiveData<Boolean>()
    private val _isDefcon4ButtonChecked = MutableLiveData<Boolean>()
    private val _isDefcon5ButtonChecked = MutableLiveData<Boolean>()
    private val _checkedButtonId = MutableLiveData<Int>()
    val checkedButtonId = _checkedButtonId
    val isDefcon1ButtonChecked = _isDefcon1ButtonChecked
    val isDefcon2ButtonChecked = _isDefcon2ButtonChecked
    val isDefcon3ButtonChecked = _isDefcon3ButtonChecked
    val isDefcon4ButtonChecked = _isDefcon4ButtonChecked
    val isDefcon5ButtonChecked = _isDefcon5ButtonChecked
    val intentObserver = Observer<Intent> { intent ->
        if (intent.action == "com.marcusrunge.mydefcon.DEFCONSTATUS_RECEIVED") {
                val data= intent.getIntExtra("data", 5)
                val source = intent.getStringExtra("source")
            onDefconStatusReceived(data, source)
        }
    }
    init {
        lifeDataManager.intent.observe(statusViewModelOwner, intentObserver )
    }

    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) setDefconStatusButton(
            inputMessage.obj as Int
        )
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        statusViewModelOwner = owner
        setDefconStatusButton(core.preferences.status)
        isDefcon1ButtonChecked.observe(statusViewModelOwner, isDefcon1ButtonCheckedObserver)
        isDefcon2ButtonChecked.observe(statusViewModelOwner, isDefcon2ButtonCheckedObserver)
        isDefcon3ButtonChecked.observe(statusViewModelOwner, isDefcon3ButtonCheckedObserver)
        isDefcon4ButtonChecked.observe(statusViewModelOwner, isDefcon4ButtonCheckedObserver)
        isDefcon5ButtonChecked.observe(statusViewModelOwner, isDefcon5ButtonCheckedObserver)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifeDataManager.intent.removeObservers(owner)
        isDefcon1ButtonChecked.removeObservers(owner)
        isDefcon2ButtonChecked.removeObservers(owner)
        isDefcon3ButtonChecked.removeObservers(owner)
        isDefcon4ButtonChecked.removeObservers(owner)
        isDefcon5ButtonChecked.removeObservers(owner)
        super.onDestroy(owner)
    }

    override fun onDefconStatusReceived(status: Int, source: String?) {
        if (source == CommunicationWorker::class.java.canonicalName) {
            val setDefconStatusMessage = Message()
            setDefconStatusMessage.what = UPDATE_VIEW
            setDefconStatusMessage.obj = status
            handler.sendMessage(setDefconStatusMessage)
        }
    }

    private fun setDefconStatusButton(status: Int) {
        when (status) {
            1 -> isDefcon1ButtonChecked.value = true
            2 -> isDefcon2ButtonChecked.value = true
            3 -> isDefcon3ButtonChecked.value = true
            4 -> isDefcon4ButtonChecked.value = true
            else -> isDefcon5ButtonChecked.value = true
        }
    }

    private fun distributeDefconStatus(status: Int) {
        core.preferences.status = status
        Intent(app.applicationContext, DefconStatusReceiver::class.java).also { intent ->
            intent.action = "com.marcusrunge.mydefcon.DEFCONSTATUS_SELECTED"
            intent.putExtra("data", status)
            intent.putExtra("source", StatusFragment::class.java.canonicalName)
            lifeDataManager.sendIntent(intent)
        }
        viewModelScope.launch { communication.network.client.sendDefconStatus(status) }
    }
}