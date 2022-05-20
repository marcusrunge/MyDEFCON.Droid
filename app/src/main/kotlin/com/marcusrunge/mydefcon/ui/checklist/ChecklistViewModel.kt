package com.marcusrunge.mydefcon.ui.checklist

import android.annotation.SuppressLint
import android.app.Application
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.CheckItemsRecyclerViewAdapter
import com.marcusrunge.mydefcon.utils.SwipeToDeleteCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    application: Application, core: Core, private val data: Data
) : ObservableViewModel(application) {
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    private val _defcon1ItemsCount= MutableLiveData("0")
    private val _defcon2ItemsCount= MutableLiveData("0")
    private val _defcon3ItemsCount= MutableLiveData("0")
    private val _defcon4ItemsCount= MutableLiveData("0")
    private val _defcon5ItemsCount= MutableLiveData("0")

    private val checkItemsObserver = Observer<MutableList<CheckItem>> {
        _checkItemsRecyclerViewAdapter.value =
            CheckItemsRecyclerViewAdapter({
                CoroutineScope(Dispatchers.IO).launch {
                   it.updated = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()
                    data.repository.checkItems.update(it)
                }
            }, {
                CoroutineScope(Dispatchers.IO).launch {
                    it.updated = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()
                    it.isDeleted=true
                    data.repository.checkItems.update(it)
                }
            })
        _checkItemsRecyclerViewAdapter.value?.setData(it)
        _itemTouchHelper.value = ItemTouchHelper(
            SwipeToDeleteCallback(
                application.applicationContext,
                _checkItemsRecyclerViewAdapter.value
            )
        )
    }

    private val statusObserver = Observer<Int> { status ->
        when (status) {
            R.id.radio_defcon1 -> checkItemsStatus = 1
            R.id.radio_defcon2 -> checkItemsStatus = 2
            R.id.radio_defcon3 -> checkItemsStatus = 3
            R.id.radio_defcon4 -> checkItemsStatus = 4
            R.id.radio_defcon5 -> checkItemsStatus = 5
        }
        if (::checkItems.isInitialized) checkItems.removeObserver(checkItemsObserver)
        if (::allCheckItems.isInitialized) allCheckItems.removeObserver(checkItemsObserver)
        checkItems = data.repository.checkItems.getAll(checkItemsStatus).getDistinct()
        allCheckItems =data.repository.checkItems.getAll().getDistinct()
        checkItems.observeForever(checkItemsObserver)
    }
    private var checkItemsStatus: Int = 5
    private var _itemTouchHelper = MutableLiveData<ItemTouchHelper>()
    private var _checkItemsRecyclerViewAdapter = MutableLiveData<CheckItemsRecyclerViewAdapter>()
    private lateinit var checkItems: LiveData<MutableList<CheckItem>>
    private lateinit var allCheckItems: LiveData<MutableList<CheckItem>>

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId
    val checkItemsRecyclerViewAdapter: LiveData<CheckItemsRecyclerViewAdapter> =
        _checkItemsRecyclerViewAdapter
    val itemTouchHelper: LiveData<ItemTouchHelper> =
        _itemTouchHelper

    val defcon1ItemsCount: LiveData<String>
        get() = _defcon1ItemsCount
    val defcon2ItemsCount: LiveData<String>
        get() = _defcon2ItemsCount
    val defcon3ItemsCount: LiveData<String>
        get() = _defcon3ItemsCount
    val defcon4ItemsCount: LiveData<String>
        get() = _defcon4ItemsCount
    val defcon5ItemsCount: LiveData<String>
        get() = _defcon5ItemsCount

    init {
        when (core.preferences.status) {
            1 -> _checkedRadioButtonId.value = R.id.radio_defcon1
            2 -> _checkedRadioButtonId.value = R.id.radio_defcon2
            3 -> _checkedRadioButtonId.value = R.id.radio_defcon3
            4 -> _checkedRadioButtonId.value = R.id.radio_defcon4
            else -> _checkedRadioButtonId.value = R.id.radio_defcon5
        }
        checkedRadioButtonId.observeForever(statusObserver)
    }

    fun onAdd() {
        CoroutineScope(Dispatchers.IO).launch {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val checkItem = CheckItem(
                id = 0, text = null,
                isChecked = false,
                isDeleted = false,
                defcon = checkItemsStatus,
                created = now.toEpochSecond(),
                updated = now.toEpochSecond()
            )
            val id = data.repository.checkItems.insert(checkItem)
            checkItem.id = id
            checkItems.value?.add(checkItem)
            val updateViewMessage = Message()
            updateViewMessage.what = UPDATE_VIEW
            updateViewMessage.obj = checkItems.value?.size?.minus(1)
            handler.sendMessage(updateViewMessage)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) _checkItemsRecyclerViewAdapter.value?.notifyItemInserted(inputMessage.obj as Int)
        else _checkItemsRecyclerViewAdapter.value?.notifyDataSetChanged()
    }

    override fun onCleared() {
        checkItems.removeObserver(checkItemsObserver)
        checkedRadioButtonId.removeObserver(statusObserver)
        super.onCleared()
    }

    private fun <T> LiveData<T>.getDistinct(): LiveData<T> {
        val distinctLiveData = MediatorLiveData<T>()
        distinctLiveData.addSource(this, object : Observer<T> {
            private var initialized = false
            private var value: T? = null
            override fun onChanged(obj: T?) {
                when {
                    !initialized -> {
                        initialized = true
                        value = obj
                        distinctLiveData.postValue(value!!)
                    }
                    obj == null && value != null || obj != value -> {
                        value = obj
                        distinctLiveData.postValue(value!!)
                    }
                }
            }
        })
        return distinctLiveData
    }

    private fun MutableList<CheckItem>.getCount(i: Int, b: Boolean): List<CheckItem> {
        return filter { checkItem -> checkItem.defcon==i && checkItem.isChecked==b }
    }
}