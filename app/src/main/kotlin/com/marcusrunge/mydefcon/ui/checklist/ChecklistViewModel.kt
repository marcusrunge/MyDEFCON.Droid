package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
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
    application: Application, private val data: Data
) : ObservableViewModel(application) {
    private val _checkItems = MutableLiveData<List<CheckItem>>(emptyList())

    val checkItemsRecyclerViewAdapter: CheckItemsRecyclerViewAdapter =
        CheckItemsRecyclerViewAdapter(_checkItems, { }, { position, id -> })

    private val observer = Observer<List<CheckItem>> {

    }

    init {
        data.repository.checkItems.getAll().observeForever(observer)
    }

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        data.repository.checkItems.getAll().removeObserver(observer)
        super.onCleared()
    }
}