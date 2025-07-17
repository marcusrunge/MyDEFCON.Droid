package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GroupPreferenceViewModel @Inject constructor(
    private val app: Application
): ObservableViewModel(app), DefaultLifecycleObserver {
    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
    }
}