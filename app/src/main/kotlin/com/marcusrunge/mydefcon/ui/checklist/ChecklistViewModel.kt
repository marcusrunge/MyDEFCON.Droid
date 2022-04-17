package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import javax.inject.Inject

class ChecklistViewModel @Inject constructor(
    application: Application
) : ObservableViewModel(application) {
    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

}