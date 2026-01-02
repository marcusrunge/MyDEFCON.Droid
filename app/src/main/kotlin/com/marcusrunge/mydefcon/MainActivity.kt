package com.marcusrunge.mydefcon

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.databinding.ActivityMainBinding
import com.marcusrunge.mydefcon.ui.main.MainViewModel
import com.marcusrunge.mydefcon.utils.BitmapUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * The main and only activity of the application.
 *
 * This activity hosts the navigation graph and handles shared UI elements like the options menu.
 * It also implements [NavController.OnDestinationChangedListener] to adapt the UI based on the
 * current navigation destination.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    @Inject
    lateinit var core: Core
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var optionsMenu: Menu? = null
    private val viewModel by viewModels<MainViewModel>()

    /**
     * Initializes the activity, sets up data binding, the navigation controller, and
     * requests notification permissions on newer Android versions.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set up edge-to-edge display
        enableEdgeToEdge()

        // Inflate and set up data binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        // Set up the window insets to handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0)
            insets
        }

        // Set the title for the open source licenses activity
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.license_title))

        // Set up the NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        navController.addOnDestinationChangedListener(this)

        // Request notification permission on Android Tiramisu (API 33) and above
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // You should implement a proper permission request flow here.
            }
        }

        // Set the status bar color and appearance
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = true
    }

    /**
     * Inflates the options menu from the resource file.
     * @param menu The options menu in which you place your items.
     * @return You must return true for the menu to be displayed.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        optionsMenu = menu
        return true
    }

    /**
     * Handles clicks on the options menu items.
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to proceed, true to consume it here.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_statusshare -> {
                shareStatus()
                true
            }

            R.id.navigation_settings -> {
                navController.navigate(R.id.navigation_settings)
                true
            }

            R.id.navigation_privacy -> {
                navController.navigate(R.id.navigation_privacy)
                true
            }
            /*R.id.navigation_licenses -> {
                startActivity(Intent(this, OssLicensesMenuActivity::class.java))
                true
            }*/
            R.id.navigation_terms -> {
                navController.navigate(R.id.navigation_terms)
                true
            }
            // Handle the Up button
            android.R.id.home -> {
                navController.popBackStack()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Creates and shares a bitmap of the current DEFCON status.
     */
    private fun shareStatus() {
        // Create a bitmap from the appropriate DEFCON status drawable
        val bitmap = BitmapUtils.createBitmapFromDrawableResource(
            applicationContext, when (core.preferences?.status) {
                1 -> R.drawable.ic_defcon1
                2 -> R.drawable.ic_defcon2
                3 -> R.drawable.ic_defcon3
                4 -> R.drawable.ic_defcon4
                5 -> R.drawable.ic_defcon5
                else -> null
            }
        )
        // Create a content URI for the bitmap
        val uri = BitmapUtils.createUriForBitmap(applicationContext, externalCacheDir, bitmap)
        // Create an intent to share the image
        val intent = BitmapUtils.createImagePngIntent(uri)
        // Start the chooser activity to let the user pick an app for sharing
        startActivity(Intent.createChooser(intent, "Share with"))
    }

    /**
     * Called when the current [NavDestination] changes.
     * This is used to show or hide the "Share" action button based on the current screen.
     */
    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        // The "Share" button should only be visible on the Status screen.
        when (destination.id) {
            R.id.navigation_status -> {
                optionsMenu?.findItem(R.id.action_statusshare)?.isVisible = true
            }

            else -> {
                optionsMenu?.findItem(R.id.action_statusshare)?.isVisible = false
            }
        }
    }

    /**
     * Hides the soft keyboard when the user touches outside of an EditText.
     */
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view: View? = currentFocus
            // Check if the focused view is an EditText
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
                // If the touch event is outside the bounds of the EditText, hide the keyboard
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    view.clearFocus()
                    val inputMethodManager =
                        getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}
