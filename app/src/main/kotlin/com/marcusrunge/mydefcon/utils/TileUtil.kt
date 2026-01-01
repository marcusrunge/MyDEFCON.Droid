package com.marcusrunge.mydefcon.utils

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap

/**
 * A custom [Drawable] that tiles another [Drawable] to fill its bounds.
 *
 * This is useful for creating patterned backgrounds from a small, repeatable image.
 *
 * @param drawable The [Drawable] to be tiled.
 * @param tileMode The [Shader.TileMode] to use for tiling (e.g., REPEAT, MIRROR, CLAMP).
 */
class TileUtil(drawable: Drawable, tileMode: Shader.TileMode) : Drawable() {

    private val paint: Paint

    init {
        // Initialize the Paint object with a BitmapShader.
        // The shader uses the bitmap from the provided drawable and the specified tile mode.
        paint = Paint().apply {
            shader = BitmapShader(getBitmap(drawable), tileMode, tileMode)
        }
    }

    /**
     * Draws the tiled drawable onto the canvas.
     * @param canvas The canvas to draw on.
     */
    override fun draw(canvas: Canvas) {
        // Draw the paint (which contains the tiled shader) onto the entire canvas.
        canvas.drawPaint(paint)
    }

    /**
     * Sets the alpha (transparency) of the drawable.
     * @param alpha The alpha value, from 0 (fully transparent) to 255 (fully opaque).
     */
    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    /**
     * Returns the opacity of the drawable.
     * This implementation always returns TRANSLUCENT.
     * @return [PixelFormat.TRANSLUCENT]
     */
    @Suppress("OVERRIDE_DEPRECATION")
    override fun getOpacity() = PixelFormat.TRANSLUCENT

    /**
     * Sets a color filter for the drawable.
     * @param colorFilter The [ColorFilter] to apply, or null to remove it.
     */
    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    /**
     * Converts a [Drawable] into a [Bitmap].
     *
     * If the drawable is already a [BitmapDrawable], its bitmap is returned directly.
     * Otherwise, a new bitmap is created, and the drawable is drawn onto it.
     *
     * @param drawable The drawable to convert.
     * @return The resulting [Bitmap].
     */
    private fun getBitmap(drawable: Drawable): Bitmap {
        // If it's already a BitmapDrawable, just return its bitmap.
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        // Otherwise, create a new bitmap and draw the drawable onto it.
        val bitmap = createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
}
