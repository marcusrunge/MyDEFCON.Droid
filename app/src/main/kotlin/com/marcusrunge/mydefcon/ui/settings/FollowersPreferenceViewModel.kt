package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.FollowersRecyclerViewAdapter
import javax.inject.Inject

class FollowersPreferenceViewModel @Inject constructor(
    app: Application, private val core: Core, private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {
    private lateinit var followersPreferenceViewModelOwner: LifecycleOwner
    private var _followersRecyclerViewAdapter = MutableLiveData<FollowersRecyclerViewAdapter>()

    val followersRecyclerViewAdapter: LiveData<FollowersRecyclerViewAdapter> =
        _followersRecyclerViewAdapter

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        followersPreferenceViewModelOwner = owner
    }

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }
}
