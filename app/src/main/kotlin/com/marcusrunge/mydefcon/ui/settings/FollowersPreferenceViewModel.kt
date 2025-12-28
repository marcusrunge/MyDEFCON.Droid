package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.FollowersRecyclerViewAdapter
import com.marcusrunge.mydefcon.utils.CheckItemsSwipeToDeleteCallback
import com.marcusrunge.mydefcon.utils.FollowersSwipeToDeleteCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel
class FollowersPreferenceViewModel @Inject constructor(
    private val app: Application, private val core: Core, private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {
    private lateinit var followersPreferenceViewModelOwner: LifecycleOwner
    private var _followersRecyclerViewAdapter = MutableLiveData<FollowersRecyclerViewAdapter>()
    private var _itemTouchHelper = MutableLiveData<ItemTouchHelper>()

    val followersRecyclerViewAdapter: LiveData<FollowersRecyclerViewAdapter> =
        _followersRecyclerViewAdapter
    val itemTouchHelper: LiveData<ItemTouchHelper> =
        _itemTouchHelper
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        followersPreferenceViewModelOwner = owner
        _itemTouchHelper.postValue(
            ItemTouchHelper(
                FollowersSwipeToDeleteCallback(
                    app.applicationContext,
                    _followersRecyclerViewAdapter.value
                )
            )
        )
    }

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }
}
