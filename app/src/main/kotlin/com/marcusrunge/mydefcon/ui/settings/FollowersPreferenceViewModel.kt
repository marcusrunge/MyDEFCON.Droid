package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import javax.inject.Inject

class FollowersPreferenceViewModel@Inject constructor(
    app: Application, private val core: Core, private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {
    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }
}