package com.marcusrunge.mydefcon.ui.settings

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
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

/**
 * ViewModel for the followers preference screen.
 *
 * This ViewModel is responsible for managing the data related to followers of a DEFCON group.
 * It communicates with the Firebase backend to fetch, update, and delete follower information.
 * It also provides the necessary data and helpers for the UI, such as the RecyclerView adapter
 * and item touch helper for swipe-to-delete functionality.
 *
 * @param app The application context.
 * @param core The core component providing access to shared preferences.
 * @param firebase The Firebase component for database interactions.
 */
@HiltViewModel
class FollowersPreferenceViewModel @Inject constructor(
    private val app: Application,
    private val core: Core,
    private val firebase: Firebase
) : ObservableViewModel(app), DefaultLifecycleObserver {

    /**
     * LiveData holding the [FollowersRecyclerViewAdapter] for the followers list.
     * The adapter is responsible for binding the follower data to the RecyclerView.
     */
    private val _followersRecyclerViewAdapter = MutableLiveData<FollowersRecyclerViewAdapter>()
    val followersRecyclerViewAdapter: LiveData<FollowersRecyclerViewAdapter> =
        _followersRecyclerViewAdapter

    /**
     * LiveData holding the [ItemTouchHelper] for handling swipe gestures on the RecyclerView.
     * This is used to implement the swipe-to-delete functionality for followers.
     */
    private val _itemTouchHelper = MutableLiveData<ItemTouchHelper>()
    val itemTouchHelper: LiveData<ItemTouchHelper> =
        _itemTouchHelper

    init {
        // Initialize the RecyclerView adapter with callbacks for item changes and deletions.
        _followersRecyclerViewAdapter.value = FollowersRecyclerViewAdapter(
            onChanged = { follower -> updateFollower(follower) },
            onDeleted = { follower -> deleteFollower(follower) })
        // Get the created DEFCON group ID from shared preferences.
        val createdDefconGroupId = core.preferences?.createdDefconGroupId
        // If a group ID exists, fetch the followers for that group from Firestore.
        if (!createdDefconGroupId.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                // Fetch followers from the remote database.
                val followers = firebase.firestore.getDefconGroupFollowers(createdDefconGroupId)
                // Create a message to update the UI on the main thread.
                val updateViewMessage = Message()
                updateViewMessage.what = UPDATE_VIEW
                updateViewMessage.arg1 = LOAD_FOLLOWERS
                updateViewMessage.obj = followers
                // Send the message to the handler to process the UI update.
                handler.sendMessage(updateViewMessage)
            }
        }

        // Initialize and post the ItemTouchHelper with the swipe-to-delete callback.
        _itemTouchHelper.postValue(
            ItemTouchHelper(
                FollowersSwipeToDeleteCallback(
                    app.applicationContext,
                    followersRecyclerViewAdapter.value
                )
            )
        )
    }

    /**
     * Deletes a follower from the DEFCON group.
     *
     * This function is called when a follower is deleted from the UI (e.g., via swipe-to-delete).
     * It retrieves the DEFCON group ID and launches a coroutine to remove the follower
     * from the group in Firestore using their installation ID.
     *
     * @param follower The [Follower] object to be deleted.
     */
    private fun deleteFollower(follower: Follower) {
        val createdDefconGroupId = core.preferences?.createdDefconGroupId
        if (!createdDefconGroupId.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                firebase.firestore.leaveDefconGroup(createdDefconGroupId, follower.installationId)
            }
        }
    }

    /**
     * Updates the status of a follower.
     *
     * This function is called when a follower's status (e.g., active/inactive) is changed in the UI.
     * It retrieves the DEFCON group ID and launches a coroutine to update the follower's
     * `isActive` status in Firestore.
     *
     * @param follower The [Follower] object with the updated status.
     */
    private fun updateFollower(follower: Follower) {
        val createdDefconGroupId = core.preferences?.createdDefconGroupId
        if (!createdDefconGroupId.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                firebase.firestore.updateFollowerStatus(
                    createdDefconGroupId,
                    follower.installationId,
                    follower.isActive
                )
            }
        }
    }

    /**
     * Updates the UI based on messages received from the handler.
     *
     * This method is part of the [ObservableViewModel] and is called when a message is
     * sent to the ViewModel's handler. It checks for the `LOAD_FOLLOWERS` message type
     * and, if the payload is a list of [Follower] objects, it updates the
     * [FollowersRecyclerViewAdapter] with the new data.
     *
     * @param inputMessage The [Message] containing data for the UI update.
     */
    override fun updateView(inputMessage: Message) {
        if (inputMessage.arg1 == LOAD_FOLLOWERS && inputMessage.obj is List<*>) {
            (inputMessage.obj as? List<*>)?.filterIsInstance<Follower>()?.let {
                _followersRecyclerViewAdapter.value?.setData(it)
            }
        }
    }

    companion object {
        /**
         * Constant used as an argument in [Message] to identify the action of loading followers.
         */
        const val LOAD_FOLLOWERS: Int = 1
    }
}
