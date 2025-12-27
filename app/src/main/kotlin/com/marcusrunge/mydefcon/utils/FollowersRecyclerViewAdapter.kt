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
) :
    RecyclerView.Adapter<FollowersRecyclerViewAdapter.ViewHolder>() {
    private var followers: MutableList<Follower> = mutableListOf()
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            R.layout.follower_row,
            parent,
            false
        )
        return ViewHolder(viewDataBinding, onChanged)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        val follower = followers[position]
        holder.bind(follower)
    }

    override fun getItemCount(): Int = followers.size

    @SuppressLint("NotifyDataSetChanged")
    fun setData(checkItems: MutableList<Follower>) {
        this.followers.clear()
        checkItems.forEach { this.followers.add(it) }
        notifyDataSetChanged()
    }

    fun setData(follower: Follower) {
        this.followers.add(follower)
        notifyItemInserted(this.followers.size - 1)
    }

    fun deleteItem(position: Int) {
        if (followers.size - 1 >= position) {
            onDeleted.invoke(followers[position])
            followers.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    class ViewHolder internal constructor(
        private val viewDataBinding: ViewDataBinding,
        private val onChanged: (follower: Follower) -> Unit
    ) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun bind(`object`: Any?) {
            viewDataBinding.setVariable(BR.follower, `object`)
            viewDataBinding.executePendingBindings()
            viewDataBinding.root.findViewById<CheckBox>(R.id.isActive)
                .setOnCheckedChangeListener { _, isChecked ->
                    (`object` as Follower).isActive = isChecked
                    onChanged.invoke(`object`)
                }
        }
    }
}