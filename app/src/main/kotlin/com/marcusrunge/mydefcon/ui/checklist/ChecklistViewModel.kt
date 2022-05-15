package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.CheckItemsRecyclerViewAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    application: Application, private val data: Data
) : ObservableViewModel(application) {
    private var checkItemsStatus: Int = 5
    private var checkItems: LiveData<MutableList<CheckItem>> = data.repository.checkItems.getAll(checkItemsStatus)
    private var _checkItemsRecyclerViewAdapter = MutableLiveData<CheckItemsRecyclerViewAdapter>()
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId
    val checkItemsRecyclerViewAdapter: LiveData<CheckItemsRecyclerViewAdapter> =
        _checkItemsRecyclerViewAdapter

    private val observer = Observer<List<CheckItem>> {
        _checkItemsRecyclerViewAdapter.value =
            CheckItemsRecyclerViewAdapter(it, { }, { position, id -> })
    }

    private val statusObserver = Observer<Int> { status ->
        when (status) {
            R.id.radio_defcon1 -> checkItemsStatus = 1
            R.id.radio_defcon2 -> checkItemsStatus = 2
            R.id.radio_defcon3 -> checkItemsStatus = 3
            R.id.radio_defcon4 -> checkItemsStatus = 4
            R.id.radio_defcon5 -> checkItemsStatus = 5
        }
        checkItems.value?.clear()

        val updateViewMessage = Message()
        updateViewMessage.what = UPDATE_VIEW
        handler.sendMessage(updateViewMessage)
    }

    init {
        checkItems.observeForever(observer)
        checkedRadioButtonId.observeForever(statusObserver)
    }

    fun onAdd(){
        CoroutineScope(Dispatchers.IO).launch {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val checkItem = CheckItem(
                id = 0, text = null,
                isChecked = false,
                isDeleted = false,
                defcon= checkItemsStatus,
                created = now.toEpochSecond(),
                updated = now.toEpochSecond()
            )
            val id = data.repository.checkItems.insert(checkItem)
            checkItem.id = id
            checkItems.value?.add(checkItem)
        }
    }

    override fun updateView(inputMessage: Message) {
        checkItemsRecyclerViewAdapter.value?.notifyDataSetChanged()
    }

    override fun onCleared() {
        checkItems.removeObserver(observer)
        checkedRadioButtonId.removeObserver(statusObserver)
        super.onCleared()
    }
}