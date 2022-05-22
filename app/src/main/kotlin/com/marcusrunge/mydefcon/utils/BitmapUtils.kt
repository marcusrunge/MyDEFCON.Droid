package com.marcusrunge.mydefcon.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

object BitmapUtils {
    fun createBitmapFromDrawableResource(context: Context, resourceId: Int?): Bitmap? {
        return if (resourceId == null) null
        else BitmapFactory.decodeResource(context.resources, resourceId)
    }
}