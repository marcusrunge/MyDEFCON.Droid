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

/**
 * A [PreferenceFragmentCompat] for displaying the app's settings.
 *
 * This fragment is responsible for inflating the preferences from an XML resource,
 * managing their state, and handling user interactions. It uses Hilt for dependency
 * injection to get instances of [Core] and [Firebase].
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
     * Called during the creation of the fragment to define the preference hierarchy.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @param rootKey If non-null, this preference fragment should be rooted at the PreferenceScreen with this key.
     */
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        groupPreferenceViewModel = ViewModelProvider(this)[GroupPreferenceViewModel::class.java]
        followersPreferenceViewModel =
            ViewModelProvider(this)[FollowersPreferenceViewModel::class.java]

        qrCodeScannerLauncher = registerForActivityResult(ScanContract()) { scanResult ->
            val scannedGroupId = scanResult.contents
            if (scannedGroupId == null) {
                Toast.makeText(requireContext(), getString(R.string.scan_cancelled), Toast.LENGTH_LONG).show()
                groupPreferenceViewModel.processQrCodeResult(null)
            } else {
                Toast.makeText(requireContext(), getString(R.string.scanned, scannedGroupId), Toast.LENGTH_LONG).show()
                groupPreferenceViewModel.processQrCodeResult(scannedGroupId)
            }
        }

        findPreference<GroupPreference>("group_preference")?.apply {
            initializeViewModel(groupPreferenceViewModel, this@SettingsFragment)
        }

        findPreference<FollowersPreference>("followers_preference")?.apply {
            initializeViewModel(followersPreferenceViewModel, this@SettingsFragment)
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The View for the fragment's UI, or null.
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
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupPreferenceViewModel.scanQrCodeEvent.observe(viewLifecycleOwner) { shouldScan ->
            if (shouldScan == true) {
                startQrCodeScanner()
            }
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This may be called even if a preference is set to its existing value.
     *
     * @param sharedPreferences The SharedPreferences that received the change.
     * @param key The key of the preference that was changed, added, or removed.
     */
    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        // TODO: Not yet implemented
    }

    /**
     * Initiates the QR code scanning process.
     *
     * This method sets up the options for the scanner, such as the desired barcode format, prompt text, camera to use, and other settings. It then launches the scanner.
     */
    private fun startQrCodeScanner() {
        val options = ScanOptions().apply {
            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
            setPrompt(getString(R.string.scan_qr_code))
            setCameraId(0)
            setBeepEnabled(true)
            setBarcodeImageEnabled(false)
            setOrientationLocked(false)
        }
        qrCodeScannerLauncher.launch(options)
    }
}
