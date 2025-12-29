package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.documents.Follower
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.FollowersRecyclerViewAdapter
import com.marcusrunge.mydefcon.utils.FollowersSwipeToDeleteCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowersPreferenceViewModel @Inject constructor(
    private val app: Application,
    private val core: Core,
    private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {

    private val _followersRecyclerViewAdapter = MutableLiveData<FollowersRecyclerViewAdapter>()
    val followersRecyclerViewAdapter: LiveData<FollowersRecyclerViewAdapter> =
        _followersRecyclerViewAdapter

    private val _itemTouchHelper = MutableLiveData<ItemTouchHelper>()
    val itemTouchHelper: LiveData<ItemTouchHelper> =
        _itemTouchHelper

    init {
        _followersRecyclerViewAdapter.value = FollowersRecyclerViewAdapter(
            onChanged = { follower -> updateFollower(follower) },
            onDeleted = { follower -> deleteFollower(follower) })
        val createdDefconGroupId = core.preferences?.createdDefconGroupId
        if (!createdDefconGroupId.isNullOrEmpty())
            viewModelScope.launch(Dispatchers.IO) {
                val followers = firebase.firestore.getDefconGroupFollowers(createdDefconGroupId)
                _followersRecyclerViewAdapter.value!!.setData(followers)
            }

        _itemTouchHelper.postValue(
            ItemTouchHelper(
                FollowersSwipeToDeleteCallback(
                    app.applicationContext,
                    followersRecyclerViewAdapter.value
                )
            )
        )
    }

    private fun initFollowers() {
        val createdDefconGroupId = core.preferences?.createdDefconGroupId
        if (!createdDefconGroupId.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val followers = firebase.firestore.getDefconGroupFollowers(createdDefconGroupId)
                val adapter = FollowersRecyclerViewAdapter(
                    onChanged = { follower -> updateFollower(follower) },
                    onDeleted = { follower -> deleteFollower(follower) })
                adapter.setData(followers.toMutableList())
                _followersRecyclerViewAdapter.postValue(adapter)
                val itemTouchHelper = ItemTouchHelper(
                    FollowersSwipeToDeleteCallback(
                        app.applicationContext,
                        adapter
                    )
                )
                _itemTouchHelper.postValue(itemTouchHelper)
            }
        }
    }

    private fun deleteFollower(follower: Follower) {
        // TODO: Implement follower deletion in Firestore
    }

    private fun updateFollower(follower: Follower) {
        // TODO: Implement follower update in Firestore
    }

    override fun updateView(inputMessage: Message) {
        // Nothing to do here yet
    }
}
