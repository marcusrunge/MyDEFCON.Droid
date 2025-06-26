package com.marcusrunge.mydefcon.ui.settings

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.preference.Preference
import androidx.preference.PreferenceViewHolder
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class GroupPreference @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : Preference(context, attrs, defStyleAttr) {
    @Inject
    lateinit var core: Core

    @Inject
    lateinit var firebase: Firebase

    override fun onBindViewHolder(holder: PreferenceViewHolder) {
        super.onBindViewHolder(holder)
        val createGroupButton = holder.findViewById(R.id.button_group_create) as? Button
        val qrCodeImageView = holder.findViewById(R.id.imageview_qrcode) as? ImageView
        val lifecycleOwner = holder.itemView.findViewTreeLifecycleOwner()
        if(core.preferences.createdDefconGroupId.isNotEmpty() && qrCodeImageView != null){
            generateAndDisplayQrCode(core.preferences.createdDefconGroupId, qrCodeImageView)
        }
        createGroupButton?.setOnClickListener {
            lifecycleOwner?.lifecycleScope?.launch {
                try {
                    val defconGroupId = withContext(Dispatchers.IO) {
                        firebase.firestore.createDefconGroup()
                    }
                    Log.d("GroupPreference", "DEFCON Group ID created: $defconGroupId")
                    if (defconGroupId.isNotEmpty() && qrCodeImageView != null) {
                        core.preferences.createdDefconGroupId = defconGroupId
                        generateAndDisplayQrCode(defconGroupId, qrCodeImageView)
                    } else if (qrCodeImageView == null) {
                        Log.e("GroupPreference", "QRCode ImageView is null")
                        Toast.makeText(
                            context,
                            "Error: QR Code display unavailable",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.w("GroupPreference", "DEFCON Group ID is empty")
                        Toast.makeText(context, "Failed to create group ID", Toast.LENGTH_SHORT)
                            .show()
                    }

                } catch (e: Exception) {
                    // Handle exceptions from Firebase or QR code generation
                    Log.e("GroupPreference", "Error during group creation or QR generation", e)
                    Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun generateAndDisplayQrCode(textToEncode: String, imageView: ImageView) {
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
            Toast.makeText(context, "Group QR code generated!", Toast.LENGTH_SHORT).show()
        } catch (e: WriterException) {
            Log.e("GroupPreference", "Error generating QR code", e)
            Toast.makeText(context, "Error generating QR code", Toast.LENGTH_SHORT).show()
        } catch (e: IllegalArgumentException) {
            // Catch error if imageView width/height is 0
            Log.e(
                "GroupPreference",
                "Error generating QR code: ImageView dimensions are invalid.",
                e
            )
            Toast.makeText(
                context,
                "Error: Could not generate QR code due to invalid image dimensions.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}