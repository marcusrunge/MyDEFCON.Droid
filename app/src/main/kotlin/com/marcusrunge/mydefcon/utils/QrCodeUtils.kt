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

object QrCodeUtils {
    fun generateAndDisplayQrCode(textToEncode: String, imageView: ImageView) {
        if (textToEncode.isEmpty()) {
            Log.e("QrCodeUtils", "Cannot generate QR code with empty text to encode.")
            return
        }
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(
                textToEncode,
                BarcodeFormat.QR_CODE,
                imageView.width.takeIf { it > 0 } ?: 512, // Use ImageView size or default
                imageView.height.takeIf { it > 0 } ?: 512 // Use ImageView size or default
            )
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bmp = createBitmap(width, height, Bitmap.Config.RGB_565)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bmp[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }
            imageView.setImageBitmap(bmp)
            imageView.visibility = ImageView.VISIBLE
        } catch (e: WriterException) {
            Log.e("GroupPreference", "Error generating QR code", e)
        } catch (e: IllegalArgumentException) {
            // Catch error if imageView width/height is 0
            Log.e(
                "GroupPreference",
                "Error generating QR code: ImageView dimensions are invalid.",
                e
            )
        }
    }
}