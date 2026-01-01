package com.marcusrunge.mydefcon.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
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
        if (checkItems.size - 1 >= position) {
            // Invoke the callback for the deletion.
            onDeleted.invoke(checkItems[position])
            // Remove the item from the local list.
            checkItems.removeAt(position)
            // Notify the adapter that an item has been removed.
            notifyItemRemoved(position)
        }
    }

    /**
     * Sets the data for the adapter, replacing any existing data.
     *
     * @param checkItems The new list of [CheckItem]s.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(checkItems: MutableList<CheckItem>) {
        this.checkItems.clear()
        this.checkItems.addAll(checkItems)
        // Notify the adapter that the entire data set has changed.
        // This is not the most efficient way but is acceptable for this use case.
        notifyDataSetChanged()
    }

    /**
     * Adds a single [CheckItem] to the end of the list.
     *
     * @param checkItem The [CheckItem] to add.
     */
    fun setData(checkItem: CheckItem) {
        this.checkItems.add(checkItem)
        // Notify the adapter that a new item has been inserted at the end of the list.
        notifyItemInserted(this.checkItems.size - 1)
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
        /**
         * Binds a [CheckItem] object to the ViewHolder's view.
         *
         * @param object The [CheckItem] data to bind.
         */
        fun bind(`object`: Any?) {
            // Set the data binding variable for the check item.
            viewDataBinding.setVariable(BR.checkitem, `object`)
            viewDataBinding.executePendingBindings()

            // Add a text changed listener to the EditText.
            viewDataBinding.root.findViewById<EditText>(R.id.editText).doAfterTextChanged {
                // When text changes, invoke the onChanged callback with the updated item.
                onChanged.invoke(`object` as CheckItem)
            }

            // Add a checked change listener to the CheckBox.
            viewDataBinding.root.findViewById<CheckBox>(R.id.checkBox)
                .setOnCheckedChangeListener { _, isChecked ->
                    // Update the isChecked property of the CheckItem.
                    (`object` as CheckItem).isChecked = isChecked
                    // Invoke the onChanged callback.
                    onChanged.invoke(`object`)
                }
        }
    }
}