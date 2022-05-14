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
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    application: Application, data: Data
) : ObservableViewModel(application) {
    private var checkItems: LiveData<List<CheckItem>> = data.repository.checkItems.getAll()
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

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        checkItems.removeObserver(observer)
        super.onCleared()
    }
}