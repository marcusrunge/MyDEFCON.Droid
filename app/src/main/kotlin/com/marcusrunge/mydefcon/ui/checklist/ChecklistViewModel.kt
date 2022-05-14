package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.CheckItemsRecyclerViewAdapter
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    application: Application, private val data: Data
) : ObservableViewModel(application) {
    private var checkItems: LiveData<MutableList<CheckItem>> = data.repository.checkItems.getAll()
    private var _checkItemsRecyclerViewAdapter = MutableLiveData<CheckItemsRecyclerViewAdapter>()
    val checkItemsRecyclerViewAdapter: LiveData<CheckItemsRecyclerViewAdapter> =
        _checkItemsRecyclerViewAdapter

    private val observer = Observer<List<CheckItem>> {
        _checkItemsRecyclerViewAdapter.value =
            CheckItemsRecyclerViewAdapter(it, { }, { position, id -> })
    }

    init {
        checkItems.observeForever(observer)
    }

    fun onAdd(){
        val now = OffsetDateTime.now(ZoneOffset.UTC)
        val checkItem =CheckItem(
            id=0, text=null,
            isChecked = false,
            isDeleted = false,
            created = now.toEpochSecond(),
            updated = now.toEpochSecond()
        )
        val id = data.repository.checkItems.insert(checkItem)
        checkItem.id=id
        checkItems.value?.add(checkItem)
    }

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        checkItems.removeObserver(observer)
        super.onCleared()
    }
}