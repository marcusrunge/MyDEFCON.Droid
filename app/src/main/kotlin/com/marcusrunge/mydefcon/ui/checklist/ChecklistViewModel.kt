package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    application: Application, private val data: Data
) : ObservableViewModel(application) {
    private val _checkItems = MutableLiveData<List<CheckItem>>(emptyList())
    val checkItems: LiveData<List<CheckItem>>
        get() = _checkItems

    private val observer = Observer<List<CheckItem>> {

    }

    init {
        data.repository.checkItems.getAll().observeForever (observer)
    }

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    override fun onCleared() {
        data.repository.checkItems.getAll().removeObserver (observer)
        super.onCleared()
    }
}