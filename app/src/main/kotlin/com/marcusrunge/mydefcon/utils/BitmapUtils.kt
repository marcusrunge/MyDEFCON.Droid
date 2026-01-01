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

/**
 * A utility object for creating and handling Bitmaps and their URIs.
 * This is primarily used for creating images to be shared from the application.
 */
object BitmapUtils {
    /**
     * Creates a Bitmap from a drawable resource.
     *
     * @param context The context to access resources.
     * @param resourceId The ID of the drawable resource.
     * @return A Bitmap representation of the drawable, scaled to 960x480, or null if the resource ID is null.
     */
    fun createBitmapFromDrawableResource(context: Context, resourceId: Int?): Bitmap? {
        // Return null if no resource ID is provided.
        return if (resourceId == null) null
        // Otherwise, get the drawable, convert it to a bitmap with a fixed size, and return it.
        else ResourcesCompat.getDrawable(context.resources, resourceId, null)?.toBitmap(960, 480)
    }

    /**
     * Saves a Bitmap to the app's external cache directory and returns a content URI for it.
     *
     * @param context The application context.
     * @param externalCacheDir The external cache directory.
     * @param bitmap The Bitmap to save.
     * @return A content URI for the saved bitmap file, suitable for sharing with other apps.
     */
    fun createUriForBitmap(context: Context, externalCacheDir: File?, bitmap: Bitmap?): Uri {
        // Define the cache path and create the directory if it doesn't exist.
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()
        // Create the file to save the bitmap to.
        val file = File(cachePath, "defcon.png")
        val fileOutputStream: FileOutputStream
        try {
            // Write the bitmap to the file in PNG format.
            fileOutputStream = FileOutputStream(file)
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Return a content URI for the file using the FileProvider.
        return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
    }

    /**
     * Creates an Intent for sharing a PNG image.
     *
     * @param uri The content URI of the image to share.
     * @return An Intent with the ACTION_SEND action, configured to share a PNG image.
     */
    fun createImagePngIntent(uri: Uri?): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        // Set flags to ensure the receiving app has permission to read the URI.
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        // Put the image URI in the intent's extra data.
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        // Set the MIME type to indicate it's a PNG image.
        intent.type = "image/png"
        return intent
    }
}
