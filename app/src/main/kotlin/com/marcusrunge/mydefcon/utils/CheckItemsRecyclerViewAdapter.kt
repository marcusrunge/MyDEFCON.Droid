package com.marcusrunge.mydefcon.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.RecyclerView
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.data.entities.CheckItem

class CheckItemsRecyclerViewAdapter(
    private val onChanged: (checkItem: CheckItem) -> Unit,
    private val onDeleted: (checkItem: CheckItem) -> Unit
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
        return ViewHolder(viewDataBinding, onChanged)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkItem = checkItems[position]
        holder.bind(checkItem)
    }

    override fun getItemCount(): Int =
        checkItems.size

    fun deleteItem(position: Int) {
        if (checkItems.size - 1 >= position) {
            onDeleted.invoke(checkItems[position])
            notifyItemRemoved(position)
        }
    }

    fun setData(checkItems: MutableList<CheckItem>) {
        //val itemCount=this.checkItems.size
        this.checkItems.clear()
        //notifyItemRangeRemoved(0,itemCount-1)
        checkItems.forEach { this.checkItems.add(it) }
        //notifyItemRangeInserted(0, checkItems.size-1)
        notifyDataSetChanged()
    }

    class ViewHolder internal constructor(
        private val viewDataBinding: ViewDataBinding,
        private val onChanged: (checkItem: CheckItem) -> Unit
    ) :
        RecyclerView.ViewHolder(viewDataBinding.root) {
        fun bind(`object`: Any?) {
            viewDataBinding.setVariable(BR.checkitem, `object`)
            viewDataBinding.executePendingBindings()
            viewDataBinding.root.findViewById<EditText>(R.id.editText).doAfterTextChanged {
                onChanged.invoke(`object` as CheckItem)
            }
            viewDataBinding.root.findViewById<CheckBox>(R.id.checkBox)
                .setOnCheckedChangeListener { _, isChecked ->
                    (`object` as CheckItem).isChecked = isChecked
                    onChanged.invoke(`object`)
                }
        }
    }
}