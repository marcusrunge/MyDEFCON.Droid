package com.marcusrunge.mydefcon.ui.scanner

import android.app.Activity
import android.os.Bundle
import android.widget.ImageButton
import com.journeyapps.barcodescanner.CaptureActivity
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.marcusrunge.mydefcon.R

/**
 * Displays the QR code scanner with an explicit cancellation button.
 *
 * Camera access, barcode decoding, permission handling and result delivery are
 * provided by the ZXing Android Embedded capture infrastructure.
 */
class QrCodeScannerActivity : CaptureActivity() {

    /**
     * Installs the custom scanner layout and returns its barcode view.
     *
     * @return The barcode view controlled by the base capture Activity.
     */
    override fun initializeContent(): DecoratedBarcodeView {
        setContentView(R.layout.activity_qr_code_scanner)
        return findViewById(R.id.zxing_barcode_scanner)
    }

    /**
     * Connects the cancellation button after the capture infrastructure has
     * initialized the custom scanner content.
     *
     * @param savedInstanceState Previously stored Activity state.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        findViewById<ImageButton>(R.id.cancel_scan_button).setOnClickListener {
            cancelScan()
        }
    }

    /**
     * Finishes scanning without returning barcode content.
     */
    private fun cancelScan() {
        setResult(RESULT_CANCELED)
        finish()
    }
}