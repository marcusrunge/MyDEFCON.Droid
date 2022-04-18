package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.os.Message
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import javax.inject.Inject

class StatusViewModel @Inject constructor(
    application: Application
) : ObservableViewModel(application) {
    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }
}