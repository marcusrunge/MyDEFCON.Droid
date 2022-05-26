package com.marcusrunge.mydefcon.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


object BitmapUtils {
    fun createBitmapFromDrawableResource(context: Context, resourceId: Int?): Bitmap? {
        return if (resourceId == null) null
        else ResourcesCompat.getDrawable(context.resources, resourceId, null)?.toBitmap(960, 480)
    }

    fun createUriForBitmap(context: Context, externalCacheDir: File?, bitmap: Bitmap?): Uri {
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()
        val file = File(cachePath, "defcon.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    fun createImagePngIntent(uri: Uri?): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/png"
        return intent
    }
}