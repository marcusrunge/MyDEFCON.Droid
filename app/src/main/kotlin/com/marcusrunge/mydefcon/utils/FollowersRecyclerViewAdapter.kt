package com.marcusrunge.mydefcon.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.marcusrunge.mydefcon.BR
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.firebase.documents.Follower

/**
 * A [RecyclerView.Adapter] for displaying a list of [Follower]s.
 *
 * This adapter is responsible for creating and binding views for each follower item,
 * and it communicates changes (e.g., when a follower's 'isActive' status changes or when a follower is deleted)
 * back to the hosting component through lambda callbacks.
 *
 * @param onChanged A lambda function to be invoked when a [Follower]'s data is modified.
 * @param onDeleted A lambda function to be invoked when a [Follower] is removed.
 */
class FollowersRecyclerViewAdapter(
    private val onChanged: (follower: Follower) -> Unit,
    private val onDeleted: (follower: Follower) -> Unit
) : RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder>() {

    private val followers = mutableListOf<Follower>()

    /**
     * Creates new [ViewHolder] instances for displaying follower items.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        // Inflate the layout for a single follower row using DataBindingUtil.
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.follower_row,
            parent,
            false
        )
        return ViewHolder(binding, onChanged)
    }

    /**
     * Binds the data at the specified position to the [ViewHolder].
     *
     * @param holder The [ViewHolder] which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // Bind the follower at the given position to the ViewHolder.
        holder.bind(followers[position])
    }

    /**
     * Returns the total number of followers in the data set held by the adapter.
     */
    override fun getItemCount(): Int = followers.size

    /**
     * Sets the data for the adapter, replacing any existing followers.
     *
     * @param items The new list of [Follower]s.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<Follower>) {
        followers.clear()
        followers.addAll(items)
        // Notify the adapter that the entire data set has changed.
        // While not the most efficient, it's simple and effective for this use case.
        notifyDataSetChanged()
    }

    /**
     * Deletes a follower at a given position from the list.
     *
     * @param position The position of the follower to delete.
     */
    fun deleteItem(position: Int) {
        // Check if the position is valid within the list's bounds.
        if (position in followers.indices) {
            // Invoke the onDeleted callback with the follower to be removed.
            onDeleted(followers[position])
            // Remove the follower from the local list.
            followers.removeAt(position)
            // Notify the adapter that an item has been removed.
            notifyItemRemoved(position)
        }
    }
    /**
     * Adds a single [Follower] to the end of the list.
     *
     * @param follower The [Follower] to add.
     */
    fun setData(follower: Follower) {
        this.followers.add(follower)
        // Notify the adapter that a new item has been inserted at the end of the list.
        notifyItemInserted(this.followers.size-1)
    }

    /**
     * Removes all followers from the adapter.
     */
    @SuppressLint("NotifyDataSetChanged")
    fun clearItems(){
        followers.clear()
        // Notify the adapter that the data set is empty.
        notifyDataSetChanged()
    }
    /**
     * A [RecyclerView.ViewHolder] that holds the view for a single [Follower].
     *
     * This ViewHolder uses data binding to bind a [Follower] to the layout and
     * handles user interactions with the 'isActive' checkbox.
     *
     * @param binding The data binding object for the item's layout.
     * @param onChanged A callback to invoke when the follower's data changes.
     */
    class ViewHolder  internal constructor(
        private val binding: ViewDataBinding,
        private val onChanged: (follower: Follower) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        /**
         * Binds a [Follower] object to the ViewHolder's view.
         *
         * @param object The [Follower] data to bind.
         */
        fun bind(`object`: Any?) {
            // Set the data binding variable for the follower.
            binding.setVariable(BR.follower, `object`)
            binding.executePendingBindings()
            // Set a listener for the 'isActive' checkbox.
            itemView.findViewById<CheckBox>(R.id.isActive)
                .setOnCheckedChangeListener { _, isChecked ->
                    // Only trigger the change if the status is actually different.
                    if ((`object` as Follower).isActive != isChecked) {
                        `object`.isActive = isChecked
                        // Invoke the onChanged callback with the updated follower data.
                        onChanged(`object`)
                    }
                }
        }
    }
}
