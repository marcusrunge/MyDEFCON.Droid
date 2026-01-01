package com.marcusrunge.mydefcon.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.marcusrunge.mydefcon.R

/**
 * An [ItemTouchHelper.SimpleCallback] for implementing swipe-to-delete functionality in a [RecyclerView].
 *
 * This callback handles the visual feedback (icon, text, and background) during the swipe gesture
 * and triggers the deletion of the item from the adapter when the swipe is completed.
 *
 * @param context The [Context] used to access resources like drawables and strings.
 * @param checkItemsRecyclerViewAdapter The adapter instance to notify when an item is deleted.
 */
class CheckItemsSwipeToDeleteCallback(
    val context: Context?,
    private val checkItemsRecyclerViewAdapter: CheckItemsRecyclerViewAdapter?
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /*or ItemTouchHelper.RIGHT*/) {
    private var icon: Drawable? = null
    private var delete: String? = null
    private var background: ColorDrawable? = null

    init {
        // Get the delete icon drawable from resources.
        icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_outline) }
        // Get the "Delete" string from resources.
        delete = context?.getString(R.string.delete)
        // Set the background color for the swipe action with some transparency.
        background = context!!.getColor(R.color.red_A700_O35).toDrawable()
    }

    /**
     * Called when an item is moved.
     * This implementation does not support drag-and-drop, so it always returns false.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * Called when an item has been swiped completely off-screen.
     *
     * @param viewHolder The ViewHolder which has been swiped.
     * @param direction The direction to which the ViewHolder is swiped.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Get the position of the swiped item.
        val position = viewHolder.bindingAdapterPosition
        // Call the deleteItem method on the adapter to remove the item.
        checkItemsRecyclerViewAdapter?.deleteItem(position)
    }

    /**
     * Called by ItemTouchHelper on RecyclerView's onDraw callback.
     * This is where the custom background, icon, and text are drawn during the swipe gesture.
     *
     * @param c The canvas which RecyclerView is drawing its children into.
     * @param recyclerView The RecyclerView to which ItemTouchHelper is attached to.
     * @param viewHolder The ViewHolder which is being interacted by the user.
     * @param dX The amount of horizontal displacement caused by user's action.
     * @param dY The amount of vertical displacement caused by user's action.
     * @param actionState The type of interaction on the View. Is either [ItemTouchHelper.ACTION_STATE_DRAG] or [ItemTouchHelper.ACTION_STATE_SWIPE].
     * @param isCurrentlyActive True if this view is currently being controlled by the user or false it is simply animating back to its original state.
     */
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView: View = viewHolder.itemView

        // Check if the swipe action was cancelled (e.g., user let go before reaching the threshold).
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
            // If cancelled, clear the canvas behind the item to remove our custom drawing.
            clearCanvas(
                c,
                itemView.right + dX,
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false)
            return
        }

        // --- Set up measurements for drawing --- //
        val backgroundCornerOffset = 20
        // Calculate margins and positions for the delete icon.
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val iconBottom = iconTop + icon!!.intrinsicHeight
        // Set up the paint for drawing the "Delete" text.
        val metrics = context?.resources?.displayMetrics
        val textPaint = TextPaint()
        val textHeight: Float
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.textSize = 24 * metrics?.density!! // Use density for scalable text size.
        val textBounds = Rect()
        delete?.length?.let { textPaint.getTextBounds(delete, 0, it, textBounds) }
        textHeight = textBounds.height().toFloat()
        val textWidth: Float = textPaint.measureText(delete!!)

        // Determine drawing bounds based on swipe direction.
        when {
            // Swiping to the right (currently disabled in constructor).
            dX > 0 -> {
                val iconLeft = itemView.left + iconMargin + icon!!.intrinsicWidth
                val iconRight = itemView.left + iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }
            // Swiping to the left.
            dX < 0 -> {
                val iconLeft = itemView.right - iconMargin - icon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            // View is not being swiped.
            else -> {
                background!!.setBounds(0, 0, 0, 0)
            }
        }

        // --- Draw on canvas --- //
        background!!.draw(c)
        icon!!.draw(c)
        delete?.let {
            c.drawText(
                it,
                icon!!.bounds.left - textWidth - 16 * metrics.density, // Position text to the left of the icon
                (textHeight / 2) + iconTop + ((iconBottom - iconTop) / 2), // Vertically center text with the icon
                textPaint
            )
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Returns the fraction of the view that must be swiped for it to be considered fully swiped.
     * @param viewHolder The ViewHolder that is being swiped.
     * @return A float value from 0 to 1.
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    /**
     * Clears a rectangular area on the canvas using a PorterDuff a CLEAR mode.
     * This is used to remove the swipe background when a swipe is cancelled.
     *
     * @param c The canvas to draw on.
     * @param left The left side of the rectangle to clear.
     * @param top The top side of the rectangle to clear.
     * @param right The right side of the rectangle to clear.
     * @param bottom The bottom side of the rectangle to clear.
     */
    private fun clearCanvas(c: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        c.drawRect(
            left,
            top,
            right,
            bottom,
            Paint().also { it.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) })
    }
}
