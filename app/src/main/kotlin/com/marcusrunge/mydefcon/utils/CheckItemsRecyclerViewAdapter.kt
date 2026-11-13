package com.marcusrunge.mydefcon.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.marcusrunge.mydefcon.BR
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.data.entities.CheckItem

/**
 * A [RecyclerView.Adapter] for displaying a list of [CheckItem]s.
 *
 * This adapter handles the creation and binding of views for each item in the checklist,
 * and communicates changes (item updates, deletions) back to the hosting component
 * via lambda callbacks.
 *
 * @param onChanged A lambda function to be invoked when a [CheckItem] is modified.
 * @param onDeleted A lambda function to be invoked when a [CheckItem] is deleted.
 */
class CheckItemsRecyclerViewAdapter(
    private val onChanged: (checkItem: CheckItem) -> Unit,
    private val onDeleted: (checkItem: CheckItem) -> Unit
) :
    RecyclerView.Adapter<CheckItemsRecyclerViewAdapter.ViewHolder>() {
    private var checkItems: MutableList<CheckItem> = mutableListOf()

    /**
     * Creates new [ViewHolder] instances for displaying items.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        // Inflate the item layout using data binding.
        val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            R.layout.check_row_item,
            parent,
            false
        )
        return ViewHolder(viewDataBinding, onChanged)
    }

    /**
     * Binds the data at the specified position to the [ViewHolder].
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkItem = checkItems[position]
        holder.bind(checkItem)
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     */
    override fun getItemCount(): Int =
        checkItems.size

    /**
     * Deletes an item at a given position from the list.
     *
     * @param position The position of the item to delete.
     */
    fun deleteItem(position: Int) {
        // Ensure the position is valid before attempting deletion.
        if (position in checkItems.indices) {
            // Invoke the callback for the deletion.
            onDeleted.invoke(checkItems[position])
            // Remove the item from the local list.
            checkItems.removeAt(position)
            // Notify the adapter that an item has been removed.
            notifyItemRemoved(position)
        }
    }

    /**
     * Sets the data for the adapter, using [DiffUtil] to update only modified items.
     *
     * @param checkItems The new list of [CheckItem]s.
     */
    fun setData(checkItems: List<CheckItem>) {
        val diffCallback = CheckItemsDiffCallback(this.checkItems, checkItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.checkItems.clear()
        this.checkItems.addAll(checkItems)
        diffResult.dispatchUpdatesTo(this)
    }

    /**
     * Adds a single [CheckItem] to the end of the list.
     *
     * @param checkItem The [CheckItem] to add.
     */
    fun setData(checkItem: CheckItem) {
        val newList = this.checkItems.toMutableList().apply { add(checkItem) }
        setData(newList)
    }

    /**
     * A [RecyclerView.ViewHolder] that holds the view for a single [CheckItem].
     *
     * This ViewHolder uses data binding to bind a [CheckItem] to the layout and
     * handles user interactions with the item's views (EditText and CheckBox).
     *
     * @param viewDataBinding The data binding object for the item's layout.
     * @param onChanged A callback to invoke when the item's data changes.
     */
    class ViewHolder internal constructor(
        private val viewDataBinding: ViewDataBinding,
        private val onChanged: (checkItem: CheckItem) -> Unit
    ) :
        RecyclerView.ViewHolder(viewDataBinding.root) {

        private var currentItem: CheckItem? = null
        private var previousText: String? = null

        init {
            val editText = viewDataBinding.root.findViewById<EditText>(R.id.editText)
            val checkBox = viewDataBinding.root.findViewById<CheckBox>(R.id.checkBox)
            val exportCheckBox = viewDataBinding.root.findViewById<CheckBox>(R.id.exportCheckBox)

            editText.doAfterTextChanged { editable ->
                val newText = editable?.toString()
                if (newText != previousText) {
                    previousText = newText
                    currentItem?.let { item ->
                        item.text = newText
                        onChanged.invoke(item)
                    }
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                currentItem?.let { item ->
                    if (item.isChecked != isChecked) {
                        item.isChecked = isChecked
                        onChanged.invoke(item)
                    }
                }
            }

            exportCheckBox.setOnCheckedChangeListener { _, isChecked ->
                currentItem?.let { item ->
                    if (item.isExport != isChecked) {
                        item.isExport = isChecked
                        onChanged.invoke(item)
                    }
                }
            }
        }

        /**
         * Binds a [CheckItem] object to the ViewHolder's view.
         *
         * @param checkItem The [CheckItem] data to bind.
         */
        fun bind(checkItem: CheckItem) {
            currentItem = checkItem
            previousText = checkItem.text
            viewDataBinding.setVariable(BR.checkitem, checkItem)
            viewDataBinding.executePendingBindings()
        }
    }

    private class CheckItemsDiffCallback(
        private val oldList: List<CheckItem>,
        private val newList: List<CheckItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem.id == newItem.id &&
                    oldItem.text == newItem.text &&
                    oldItem.isChecked == newItem.isChecked &&
                    oldItem.isExport == newItem.isExport &&
                    oldItem.defcon == newItem.defcon
        }
    }
}