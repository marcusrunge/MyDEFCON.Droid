package com.marcusrunge.mydefcon.ui.settings

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceFragmentCompat
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    @Inject
    lateinit var core: Core

    @Inject
    lateinit var firebase: Firebase
    private lateinit var groupPreferenceViewModel: GroupPreferenceViewModel

    private lateinit var qrCodeScannerLauncher: ActivityResultLauncher<ScanOptions>

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        groupPreferenceViewModel = ViewModelProvider(this)[GroupPreferenceViewModel::class.java]
        qrCodeScannerLauncher = registerForActivityResult(ScanContract()) { scanResult ->
            if (scanResult.contents == null) {
                Toast.makeText(requireContext(),
                    getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show()
                groupPreferenceViewModel.processQrCodeResult(null)
            } else {
                val scannedGroupId = scanResult.contents
                Toast.makeText(requireContext(),
                    getString(R.string.scanned, scannedGroupId), Toast.LENGTH_LONG).show() // Optional: for debugging
                groupPreferenceViewModel.processQrCodeResult(scannedGroupId)
            }
        }
        val groupPreference = findPreference<GroupPreference>("group_preference")
        groupPreference?.apply {
            initializeViewModel(groupPreferenceViewModel, this@SettingsFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundColor(Color.argb(0.698f, 0f, 0f, 0f))
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupPreferenceViewModel.scanQrCodeEvent.observe(viewLifecycleOwner) { shouldScan ->
            if (shouldScan == true) {
                startQrCodeScanner()
            }
        }
    }

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        TODO("Not yet implemented")
    }

    private fun startQrCodeScanner() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt(getString(R.string.scan_qr_code))
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setBarcodeImageEnabled(false)
        options.setOrientationLocked(false)
        qrCodeScannerLauncher.launch(options)
    }
}