package com.marcusrunge.mydefcon.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.models.CheckItem

class CheckItemsRecyclerViewAdapter(
    private val checkItems: MutableList<CheckItem>,
    private val onChanged: (id: Long) -> Unit,
    private val onDeleted: (position: Int, id: Long) -> Unit
) :
    RecyclerView.Adapter<CheckItemsRecyclerViewAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            layoutInflater,
            R.layout.check_row_item,
            parent,
            false
        )
        return ViewHolder(viewDataBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkItem = checkItems[position]
        holder.bind(checkItem)
    }

    override fun getItemCount(): Int = checkItems.size

    fun deleteItem(position: Int) {
        onDeleted.invoke(position, checkItems[position].id.toLong())
    }

    class ViewHolder internal constructor(private val viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun bind(`object`: Any?) {
            viewDataBinding.setVariable(BR.checkitem, `object`)
            viewDataBinding.executePendingBindings()
        }
    }
}