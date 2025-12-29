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

class FollowersRecyclerViewAdapter(
    private val onChanged: (follower: Follower) -> Unit,
    private val onDeleted: (follower: Follower) -> Unit
) : RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder>() {

    private val followers = mutableListOf<Follower>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            inflater,
            R.layout.follower_row,
            parent,
            false
        )
        return ViewHolder(binding, onChanged)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(followers[position])
    }

    override fun getItemCount(): Int = followers.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(items: List<Follower>) {
        followers.clear()
        followers.addAll(items)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        if (position in followers.indices) {
            onDeleted(followers[position])
            followers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder  internal constructor(
        private val binding: ViewDataBinding,
        private val onChanged: (follower: Follower) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(`object`: Any?) {
            binding.setVariable(BR.follower, `object`)
            binding.executePendingBindings()
            itemView.findViewById<CheckBox>(R.id.isActive)
                .setOnCheckedChangeListener { _, isChecked ->
                    if ((`object` as Follower).isActive != isChecked) {
                        `object`.isActive = isChecked
                        onChanged(`object`)
                    }
                }
        }
    }
}
