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
 * An [ItemTouchHelper.SimpleCallback] for implementing swipe-to-delete functionality in a [RecyclerView]
 * that displays a list of followers.
 *
 * This callback manages the visual aspects of the swipe gesture (icon, text, and background)
 * and triggers the deletion of the follower from the adapter upon completion of the swipe.
 *
 * @param context The [Context] used for accessing resources like drawables and strings.
 * @param followersRecyclerViewAdapter The adapter instance to be notified when a follower is deleted.
 */
class FollowersSwipeToDeleteCallback(
    val context: Context?,
    private val followersRecyclerViewAdapter: FollowersRecyclerViewAdapter?
) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT /*or ItemTouchHelper.RIGHT*/) {
    private var icon: Drawable? = null
    private var delete: String? = null
    private var background: ColorDrawable? = null

    init {
        // Initialize the icon, text, and background for the swipe animation.
        icon = context?.let { ContextCompat.getDrawable(it, R.drawable.ic_delete_outline) }
        delete = context?.getString(R.string.delete)
        background = context!!.getColor(R.color.red_A700_O35).toDrawable()
    }

    /**
     * Called when an item is moved (dragged and dropped).
     * This functionality is not supported here, so it always returns false.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    /**
     * Called when a ViewHolder is swiped by the user.
     *
     * @param viewHolder The ViewHolder that was swiped.
     * @param direction The direction in which the ViewHolder was swiped.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        // Notify the adapter to delete the item at the swiped position.
        followersRecyclerViewAdapter?.deleteItem(position)
    }

    /**
     * Custom drawing method to draw the swipe background, icon, and text.
     * This is called by the ItemTouchHelper while the user is swiping a view.
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

        // If the swipe is cancelled, clear the canvas and return.
        val isCancelled = dX == 0f && !isCurrentlyActive
        if (isCancelled) {
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
        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon!!.intrinsicHeight) / 2
        val iconBottom = iconTop + icon!!.intrinsicHeight
        val metrics = context?.resources?.displayMetrics
        val textPaint = TextPaint()
        val textHeight: Float
        textPaint.color = Color.WHITE
        textPaint.isAntiAlias = true
        textPaint.textSize = 24 * metrics?.density!! // Use density for scalable text.
        val textBounds = Rect()
        delete?.length?.let { textPaint.getTextBounds(delete, 0, it, textBounds) }
        textHeight = textBounds.height().toFloat()
        val textWidth: Float = textPaint.measureText(delete!!)

        // Adjust drawing bounds based on the swipe direction.
        when {
            dX > 0 -> { // Swiping to the right (currently disabled).
                val iconLeft = itemView.left + iconMargin + icon!!.intrinsicWidth
                val iconRight = itemView.left + iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.setBounds(
                    itemView.left, itemView.top,
                    itemView.left + dX.toInt() + backgroundCornerOffset,
                    itemView.bottom
                )
            }

            dX < 0 -> { // Swiping to the left.
                val iconLeft = itemView.right - iconMargin - icon!!.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon!!.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background!!.setBounds(
                    itemView.right + dX.toInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }

            else -> { // View is not being swiped.
                background!!.setBounds(0, 0, 0, 0)
            }
        }

        // --- Draw everything on the canvas --- //
        background!!.draw(c)
        icon!!.draw(c)
        delete?.let {
            c.drawText(
                it,
                icon!!.bounds.left - textWidth - 16 * metrics.density, // Position text left of the icon.
                (textHeight / 2) + iconTop + ((iconBottom - iconTop) / 2), // Vertically center text with the icon.
                textPaint
            )
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    /**
     * Defines the fraction of the view that must be swiped for the action to be triggered.
     * @return A float value from 0 to 1, where 1 means the full width must be swiped.
     */
    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 0.7f
    }

    /**
     * Clears a rectangular area on the canvas.
     * This is used to remove the swipe background when the action is cancelled.
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
