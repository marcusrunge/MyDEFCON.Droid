package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.models.CheckItem
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel @Inject constructor(
    application: Application, private val data: Data
) : ObservableViewModel(application) {
    private val _checkItems = MutableLiveData<List<CheckItem>>(emptyList())
    init {
        loadData()
    }

    val checkItems: LiveData<List<CheckItem>>
        get() = _checkItems


    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    private fun loadData() {
        viewModelScope.launch {
            val entities = data.repository.checkItems.getAll()
        }
    }
}