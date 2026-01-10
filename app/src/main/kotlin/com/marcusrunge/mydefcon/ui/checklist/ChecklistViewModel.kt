package com.marcusrunge.mydefcon.ui.checklist

import android.app.Application
import android.os.Message
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import com.marcusrunge.mydefcon.utils.CheckItemsRecyclerViewAdapter
import com.marcusrunge.mydefcon.utils.CheckItemsSwipeToDeleteCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the checklist screen.
 *
 * This class is responsible for preparing and managing the data for the [ChecklistFragment].
 * It handles business logic, state management, and interactions with the data layer.
 *
 * @param app The application instance.
 * @param core The core component for accessing shared preferences.
 * @param data The data component for accessing the database.
 */
@HiltViewModel
class ChecklistViewModel @Inject constructor(
    app: Application,
    private val core: Core,
    private val data: Data
) : ObservableViewModel(app), DefaultLifecycleObserver {

    private lateinit var checklistViewModelOwner: LifecycleOwner

    // Backing properties for LiveData
    private val _checkedRadioButtonId = MutableLiveData<Int>()
    private val _defcon1ItemsCount = MutableLiveData("0")
    private val _defcon2ItemsCount = MutableLiveData("0")
    private val _defcon3ItemsCount = MutableLiveData("0")
    private val _defcon4ItemsCount = MutableLiveData("0")
    private val _defcon5ItemsCount = MutableLiveData("0")
    private val _defcon1ItemsCountBackgroundColorResource = MutableLiveData<Int>()
    private val _defcon2ItemsCountBackgroundColorResource = MutableLiveData<Int>()
    private val _defcon3ItemsCountBackgroundColorResource = MutableLiveData<Int>()
    private val _defcon4ItemsCountBackgroundColorResource = MutableLiveData<Int>()
    private val _defcon5ItemsCountBackgroundColorResource = MutableLiveData<Int>()
    private var _itemTouchHelper = MutableLiveData<ItemTouchHelper>()
    private var _checkItemsRecyclerViewAdapter = MutableLiveData<CheckItemsRecyclerViewAdapter>()

    // LiveData exposed to the View
    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId
    val checkItemsRecyclerViewAdapter: LiveData<CheckItemsRecyclerViewAdapter> = _checkItemsRecyclerViewAdapter
    val itemTouchHelper: LiveData<ItemTouchHelper> = _itemTouchHelper
    val defcon1ItemsCount: LiveData<String> get() = _defcon1ItemsCount
    val defcon2ItemsCount: LiveData<String> get() = _defcon2ItemsCount
    val defcon3ItemsCount: LiveData<String> get() = _defcon3ItemsCount
    val defcon4ItemsCount: LiveData<String> get() = _defcon4ItemsCount
    val defcon5ItemsCount: LiveData<String> get() = _defcon5ItemsCount
    val defcon1ItemsCountBackgroundColorResource: LiveData<Int> get() = _defcon1ItemsCountBackgroundColorResource
    val defcon2ItemsCountBackgroundColorResource: LiveData<Int> get() = _defcon2ItemsCountBackgroundColorResource
    val defcon3ItemsCountBackgroundColorResource: LiveData<Int> get() = _defcon3ItemsCountBackgroundColorResource
    val defcon4ItemsCountBackgroundColorResource: LiveData<Int> get() = _defcon4ItemsCountBackgroundColorResource
    val defcon5ItemsCountBackgroundColorResource: LiveData<Int> get() = _defcon5ItemsCountBackgroundColorResource

    // Observers
    private var _checkItemsObserver: Observer<MutableList<CheckItem>>? = null
    private val checkItemsObserver: Observer<MutableList<CheckItem>>
        get() {
            if (_checkItemsObserver == null) {
                _checkItemsObserver = Observer {
                    _checkItemsRecyclerViewAdapter.value?.setData(it)
                }
            }
            return _checkItemsObserver ?: throw AssertionError("Set to null by another thread")
        }

    private var _statusObserver: Observer<Int>? = null
    private val statusObserver: Observer<Int>
        get() {
            if (_statusObserver == null) {
                _statusObserver = Observer { status ->
                    checkItemsStatus = when (status) {
                        R.id.radio_defcon1 -> 1
                        R.id.radio_defcon2 -> 2
                        R.id.radio_defcon3 -> 3
                        R.id.radio_defcon4 -> 4
                        else -> 5
                    }
                    loadCheckItemsForStatus()
                }
            }
            return _statusObserver ?: throw AssertionError("Set to null by another thread")
        }

    private var _allCheckItemsObserver: Observer<MutableList<CheckItem>>? = null
    private val allCheckItemsObserver: Observer<MutableList<CheckItem>>
        get() {
            if (_allCheckItemsObserver == null) {
                _allCheckItemsObserver = Observer { items ->
                    updateDefconCounts(items)
                }
            }
            return _allCheckItemsObserver ?: throw AssertionError("Set to null by another thread")
        }

    private var checkItemsStatus: Int = 5
    private lateinit var checkItems: LiveData<MutableList<CheckItem>>
    private lateinit var allCheckItems: LiveData<MutableList<CheckItem>>

    init {
        setupRecyclerViewAdapter()
        setupItemTouchHelper()
    }

    /**
     * Sets the [LifecycleOwner] for this ViewModel.
     */
    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        checklistViewModelOwner = owner
    }

    /**
     * Called when the fragment is resumed. Initializes observers and loads data.
     */
    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        _checkedRadioButtonId.value = when (core.preferences?.status) {
            1 -> R.id.radio_defcon1
            2 -> R.id.radio_defcon2
            3 -> R.id.radio_defcon3
            4 -> R.id.radio_defcon4
            else -> R.id.radio_defcon5
        }
        checkedRadioButtonId.observeForever(statusObserver)
        allCheckItems = data.repository.checkItems.getAllLive().map { it.toMutableList() }.getDistinct()
        allCheckItems.observeForever(allCheckItemsObserver)
    }

    /**
     * Called when the ViewModel is about to be destroyed.
     * Removes observers to prevent memory leaks.
     */
    override fun onCleared() {
        if (::checkItems.isInitialized) {
            checkItems.removeObserver(checkItemsObserver)
        }
        if (::allCheckItems.isInitialized) {
            allCheckItems.removeObserver(allCheckItemsObserver)
        }
        checkedRadioButtonId.removeObserver(statusObserver)
        super.onCleared()
    }

    /**
     * Handles adding a new check item to the database.
     * A new [CheckItem] is created with the current `checkItemsStatus` and inserted.
     * The view is then notified to display the new item.
     */
    fun onAdd() {
        viewModelScope.launch(Dispatchers.IO) {
            val now = OffsetDateTime.now(ZoneOffset.UTC)
            val checkItem = CheckItem(
                id = 0,
                uuid = UUID.randomUUID().toString(),
                text = null,
                isChecked = false,
                isDeleted = false,
                defcon = checkItemsStatus,
                created = now.toEpochSecond(),
                updated = now.toEpochSecond()
            )
            val id = data.repository.checkItems.insert(checkItem)
            checkItem.id = id
            checkItems.value?.add(checkItem)

            // Inform the view to update
            val updateViewMessage =
                Message.obtain(handler, UPDATE_VIEW, LOAD_CHECKLIST, 0, checkItem)
            handler.sendMessage(updateViewMessage)
        }
    }

    /**
     * Updates the UI based on messages received from background threads.
     * This handles adding a single item or reloading the entire list.
     * @param inputMessage The message containing update information.
     */
    override fun updateView(inputMessage: Message) {
        when (inputMessage.arg1) {
            LOAD_CHECKLIST -> {
                if (inputMessage.obj is CheckItem) {
                    _checkItemsRecyclerViewAdapter.value?.setData(inputMessage.obj as CheckItem)
                }
            }
            RELOAD_CHECKLIST -> {
                if (inputMessage.obj is List<*>) {
                    allCheckItems.value?.clear()
                    val newCheckItems: MutableList<CheckItem> = mutableListOf()
                    (inputMessage.obj as List<*>).forEach {
                        val item = it as CheckItem
                        allCheckItems.value?.add(item)
                        if (item.defcon == checkItemsStatus) newCheckItems.add(item)
                    }
                    _checkItemsRecyclerViewAdapter.value?.setData(newCheckItems)
                }
            }
        }
    }

    /**
     * Initializes the [CheckItemsRecyclerViewAdapter] with callbacks for item updates and deletions.
     */
    private fun setupRecyclerViewAdapter() {
        _checkItemsRecyclerViewAdapter.value = CheckItemsRecyclerViewAdapter({ item ->
            // On item updated (e.g., checkbox toggled)
            viewModelScope.launch(Dispatchers.IO) {
                item.updated = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()
                data.repository.checkItems.update(item)
                // Refresh all items to recalculate counts.
                allCheckItems = data.repository.checkItems.getAllLive().map { it.toMutableList() }.getDistinct()
            }
        }, { item ->
            // On item deleted (swiped away)
            viewModelScope.launch(Dispatchers.IO) {
                item.updated = OffsetDateTime.now(ZoneOffset.UTC).toEpochSecond()
                item.isDeleted = true
                data.repository.checkItems.update(item)
                // Refresh all items to recalculate counts.
                allCheckItems = data.repository.checkItems.getAllLive().map { it.toMutableList() }.getDistinct()
            }
        })
    }

    /**
     * Initializes the [ItemTouchHelper] for swipe-to-delete functionality in the RecyclerView.
     */
    private fun setupItemTouchHelper() {
        val swipeHandler = CheckItemsSwipeToDeleteCallback(
            getApplication(),
            _checkItemsRecyclerViewAdapter.value
        )
        _itemTouchHelper.postValue(ItemTouchHelper(swipeHandler))
    }

    /**
     * Loads the check items from the database for the current DEFCON status.
     * It removes any existing observer, fetches the new data, and observes it.
     */
    private fun loadCheckItemsForStatus() {
        if (::checkItems.isInitialized) {
            checkItems.removeObserver(checkItemsObserver)
        }
        checkItems = data.repository.checkItems.getAllLive(checkItemsStatus).map { it.toMutableList() }.getDistinct()
        checkItems.observe(checklistViewModelOwner, checkItemsObserver)
    }

    /**
     * Updates the counts and background colors for each DEFCON level based on the current items.
     * @param items The list of all check items.
     */
    private fun updateDefconCounts(items: List<CheckItem>) {
        val status = core.preferences?.status ?: 5

        // Calculate checked and unchecked counts for each DEFCON level
        val counts = (1..5).map { defcon ->
            items.getCountsForDefcon(defcon, true) to items.getCountsForDefcon(defcon, false)
        }
        val (defcon1Checked, defcon1Unchecked) = counts[0]
        val (defcon2Checked, defcon2Unchecked) = counts[1]
        val (defcon3Checked, defcon3Unchecked) = counts[2]
        val (defcon4Checked, defcon4Unchecked) = counts[3]
        val (defcon5Checked, defcon5Unchecked) = counts[4]

        // Update LiveData for counts based on the current DEFCON status
        _defcon1ItemsCount.value = if (status <= 1) defcon1Unchecked.toString() else "0"
        _defcon2ItemsCount.value = if (status <= 2) defcon2Unchecked.toString() else "0"
        _defcon3ItemsCount.value = if (status <= 3) defcon3Unchecked.toString() else "0"
        _defcon4ItemsCount.value = if (status <= 4) defcon4Unchecked.toString() else "0"
        _defcon5ItemsCount.value = defcon5Unchecked.toString()

        // Update LiveData for background colors
        _defcon1ItemsCountBackgroundColorResource.value =
            getBackgroundColor(status > 1, defcon1Unchecked, defcon1Checked)
        _defcon2ItemsCountBackgroundColorResource.value =
            getBackgroundColor(status > 2, defcon2Unchecked, defcon2Checked)
        _defcon3ItemsCountBackgroundColorResource.value =
            getBackgroundColor(status > 3, defcon3Unchecked, defcon3Checked)
        _defcon4ItemsCountBackgroundColorResource.value =
            getBackgroundColor(status > 4, defcon4Unchecked, defcon4Checked)
        _defcon5ItemsCountBackgroundColorResource.value =
            getBackgroundColor(false, defcon5Unchecked, defcon5Checked)
    }

    /**
     * Determines the background color resource based on item counts and whether the DEFCON level has passed.
     * @param isDefconPassed Whether the DEFCON level is already passed.
     * @param uncheckedCount The number of unchecked items.
     * @param checkedCount The number of checked items.
     * @return The color resource ID.
     */
    private fun getBackgroundColor(
        isDefconPassed: Boolean,
        uncheckedCount: Int,
        checkedCount: Int
    ): Int {
        return when {
            isDefconPassed || uncheckedCount == 0 -> R.color.green_700_O85
            uncheckedCount > 0 && checkedCount > 0 -> R.color.yellow_900_085
            else -> R.color.red_700_085
        }
    }

    /**
     * An extension function to count items in a list of [CheckItem] by DEFCON level and checked status.
     * @param defcon The DEFCON level to filter by.
     * @param isChecked The checked status to filter by.
     * @return The count of matching items.
     */
    private fun List<CheckItem>.getCountsForDefcon(defcon: Int, isChecked: Boolean): Int {
        return this.count { it.defcon == defcon && it.isChecked == isChecked }
    }

    /**
     * An extension function to prevent [LiveData] from emitting the same value consecutively.
     * This is useful for preventing unnecessary UI updates.
     * @return A new [LiveData] instance that only emits distinct values.
     */
    private fun <T> LiveData<T>.getDistinct(): LiveData<T> {
        val distinctLiveData = MediatorLiveData<T>()
        distinctLiveData.addSource(this, object : Observer<T> {
            private var initialized = false
            private var liveValue: T? = null
            override fun onChanged(value: T) {
                if (!initialized) {
                    initialized = true
                    liveValue = value
                    distinctLiveData.postValue(liveValue)
                } else if ((value == null && liveValue != null) || value != liveValue) {
                    liveValue = value
                    distinctLiveData.postValue(liveValue)
                }
            }
        })
        return distinctLiveData
    }

    companion object {
        const val LOAD_CHECKLIST: Int = 1
        const val RELOAD_CHECKLIST: Int = 2
    }
}
