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
import com.marcusrunge.mydefcon.ui.scanner.QrCodeScannerActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * A [PreferenceFragmentCompat] for displaying the application's settings.
 *
 * This Fragment inflates the preference hierarchy, connects the custom
 * preferences to their ViewModels and coordinates QR code scanning.
 */
@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    @Inject
    lateinit var core: Core

    @Inject
    lateinit var firebase: Firebase

    private lateinit var groupPreferenceViewModel: GroupPreferenceViewModel
    private lateinit var followersPreferenceViewModel: FollowersPreferenceViewModel
    private lateinit var qrCodeScannerLauncher: ActivityResultLauncher<ScanOptions>

    /**
     * Creates the preference hierarchy and registers the scanner result contract.
     *
     * @param savedInstanceState Previously stored Fragment state.
     * @param rootKey Optional root preference key.
     */
    override fun onCreatePreferences(
        savedInstanceState: Bundle?,
        rootKey: String?
    ) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        groupPreferenceViewModel =
            ViewModelProvider(this)[GroupPreferenceViewModel::class.java]

        followersPreferenceViewModel =
            ViewModelProvider(this)[FollowersPreferenceViewModel::class.java]

        qrCodeScannerLauncher =
            registerForActivityResult(ScanContract()) { scanResult ->
                processQrCodeScanResult(scanResult.contents)
            }

        findPreference<GroupPreference>(GROUP_PREFERENCE_KEY)?.initializeViewModel(
            groupPreferenceViewModel,
            this
        )

        findPreference<FollowersPreference>(FOLLOWERS_PREFERENCE_KEY)?.initializeViewModel(
            followersPreferenceViewModel,
            this
        )
    }

    /**
     * Creates the Fragment view and applies the existing translucent background.
     *
     * @param inflater Layout inflater.
     * @param container Parent view.
     * @param savedInstanceState Previously stored Fragment state.
     * @return The created preference view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view.setBackgroundColor(Color.argb(0.698f, 0f, 0f, 0f))
        return view
    }

    /**
     * Observes scanner launch requests after the Fragment view has been created.
     *
     * @param view The created Fragment view.
     * @param savedInstanceState Previously stored Fragment state.
     */
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        groupPreferenceViewModel.scanQrCodeEvent.observe(viewLifecycleOwner) { shouldScan ->
            if (shouldScan != true) {
                return@observe
            }

            /*
             * Consume the launch request before starting the Activity. This
             * prevents recreation of the Fragment from starting a second scanner.
             */
            groupPreferenceViewModel.onQrCodeScannerLaunchHandled()
            startQrCodeScanner()
        }
    }

    /**
     * Receives changes to shared preferences.
     *
     * @param sharedPreferences Changed preference storage.
     * @param key Changed preference key.
     */
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        // No direct shared preference changes are currently handled here.
    }

    /**
     * Launches the customized ZXing QR code scanner Activity.
     */
    private fun startQrCodeScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt(getString(R.string.scan_qr_code))
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
            setOrientationLocked(false)
            setCaptureActivity(QrCodeScannerActivity::class.java)
        }

        qrCodeScannerLauncher.launch(options)
    }

    /**
     * Processes a scanner result and forwards valid group IDs to the ViewModel.
     *
     * @param scannedGroupId Scanned content, or null when scanning was cancelled.
     */
    private fun processQrCodeScanResult(scannedGroupId: String?) {
        val normalizedGroupId = scannedGroupId?.trim()

        if (normalizedGroupId.isNullOrEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.scan_cancelled),
                Toast.LENGTH_LONG
            ).show()

            groupPreferenceViewModel.processQrCodeResult(null)
            return
        }

        Toast.makeText(
            requireContext(),
            getString(R.string.scanned, normalizedGroupId),
            Toast.LENGTH_LONG
        ).show()

        groupPreferenceViewModel.processQrCodeResult(normalizedGroupId)
    }

    private companion object {
        const val GROUP_PREFERENCE_KEY = "group_preference"
        const val FOLLOWERS_PREFERENCE_KEY = "followers_preference"
    }
}