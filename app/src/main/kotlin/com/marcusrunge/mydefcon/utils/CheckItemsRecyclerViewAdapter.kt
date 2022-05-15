package com.marcusrunge.mydefcon.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.RadioGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.data.entities.CheckItem

class CheckItemsRecyclerViewAdapter(
    private val onChanged: (id: Long) -> Unit,
    private val onDeleted: (position: Int, id: Long) -> Unit
) :
    RecyclerView.Adapter<CheckItemsRecyclerViewAdapter.ViewHolder>() {
    private var checkItems: MutableList<CheckItem> = mutableListOf()

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

    override fun getItemCount(): Int =
        checkItems.size

    fun deleteItem(position: Int) {
        onDeleted.invoke(position, checkItems[position].id.toLong())
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(checkItems: MutableList<CheckItem>) {
        this.checkItems=checkItems
        notifyDataSetChanged()
    }

    class ViewHolder internal constructor(private val viewDataBinding: ViewDataBinding) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun bind(`object`: Any?) {
            viewDataBinding.setVariable(BR.checkitem, `object`)
            viewDataBinding.executePendingBindings()
        }
    }
}