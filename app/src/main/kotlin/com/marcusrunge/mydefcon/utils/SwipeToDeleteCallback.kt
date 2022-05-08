package com.marcusrunge.mydefcon.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


class SwipeToDeleteCallback(
    val context: Context?,
    val checkItemsRecyclerViewAdapter: CheckItemsRecyclerViewAdapter?
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /*or ItemTouchHelper.RIGHT*/) {
    private var icon: Drawable? = null
    private var delete: String? = null
    private var background: ColorDrawable? = null
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        TODO("Not yet implemented")
    }

    init {
        /*icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_outline) }
        delete = context?.getString(R.string.delete)
        background = ColorDrawable(context!!.getColor(R.color.delete))*/
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        checkItemsRecyclerViewAdapter?.deleteItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView: View = viewHolder.itemView
        val backgroundCornerOffset = 20
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val iconBottom = iconTop + icon!!.intrinsicHeight
        val metrics = context?.resources?.displayMetrics
        val textPaint = TextPaint()
        val textHeight: Float
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.textSize = 24 * metrics?.density!!
        val textBounds = Rect()
        delete?.length?.let { textPaint.getTextBounds(delete, 0, it, textBounds) }
        textHeight = textBounds.height().toFloat()
        val textWidth: Float = textPaint.measureText(delete)

        when {
            dX > 0 -> { // Swiping to the right
                val iconLeft = itemView.left + iconMargin + icon!!.intrinsicWidth
                val iconRight = itemView.left + iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }
            dX < 0 -> { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> { // view is unSwiped
                background!!.setBounds(0, 0, 0, 0)
            }
        }

        background!!.draw(c)
        icon!!.draw(c)
        delete?.let {
            c.drawText(
                it,
                icon!!.bounds.left - textWidth - 16 * metrics.density,
                viewHolder.itemView.height - ((viewHolder.itemView.height - textHeight) / 2),
                textPaint
            )
        }
    }
}