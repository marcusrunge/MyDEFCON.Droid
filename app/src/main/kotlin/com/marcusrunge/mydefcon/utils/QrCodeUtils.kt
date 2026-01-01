package com.marcusrunge.mydefcon.utils

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter

/**
 * A utility object for generating QR codes.
 * This object provides a simple way to create a QR code bitmap from a string
 * and display it in an ImageView.
 */
object QrCodeUtils {
    /**
     * Generates a QR code from a given string and displays it in an [ImageView].
     *
     * If the input string is empty, an error is logged, and the function returns.
     * The size of the generated QR code is determined by the dimensions of the provided [ImageView].
     * If the ImageView's dimensions are not available (e.g., not yet measured), a default size of 512x512 is used.
     *
     * @param textToEncode The string to be encoded into the QR code.
     * @param imageView The [ImageView] where the generated QR code will be displayed.
     */
    fun generateAndDisplayQrCode(textToEncode: String, imageView: ImageView) {
        // Do not proceed if the text to encode is empty.
        if (textToEncode.isEmpty()) {
            Log.e("QrCodeUtils", "Cannot generate QR code with empty text to encode.")
            return
        }
        val writer = QRCodeWriter()
        try {
            // Encode the text into a QR code BitMatrix.
            // Use the ImageView's dimensions or a default size of 512x512.
            val bitMatrix = writer.encode(
                textToEncode,
                BarcodeFormat.QR_CODE,
                imageView.width.takeIf { it > 0 } ?: 512,
                imageView.height.takeIf { it > 0 } ?: 512
            )
            // Create a bitmap from the BitMatrix.
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
            // Iterate over each pixel of the BitMatrix and set the color in the bitmap.
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            // Set the generated bitmap to the ImageView and make it visible.
            imageView.setImageBitmap(bmp)
            imageView.visibility = ImageView.VISIBLE
        } catch (e: WriterException) {
            // Log errors that occur during the QR code generation process.
            Log.e("GroupPreference", "Error generating QR code", e)
        } catch (e: IllegalArgumentException) {
            // Catch errors if the ImageView's dimensions are invalid (e.g., width/height is 0).
            Log.e(
                "GroupPreference",
                "Error generating QR code: ImageView dimensions are invalid.",
                e
            )
        }
    }
}
