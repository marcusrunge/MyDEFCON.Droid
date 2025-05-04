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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.databinding.ActivityMainBinding
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.ui.main.MainViewModel
import com.marcusrunge.mydefcon.utils.BitmapUtils
import com.marcusrunge.mydefcon.worker.CommunicationWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {
    @Inject
    lateinit var core: Core
    @Inject
    lateinit var firebase: Firebase
    @Inject
    lateinit var communication: Communication
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private var optionsMenu: Menu? = null
    private val viewModel by viewModels<MainViewModel>()
    private val communicationWorkRequest: WorkRequest =
        OneTimeWorkRequestBuilder<CommunicationWorker>().build()
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                WorkManager
                    .getInstance(this)
                    .enqueue(communicationWorkRequest)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.license_title))
        navController =
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment).navController
        navController.addOnDestinationChangedListener(this)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) -> {
                    WorkManager
                        .getInstance(this)
                        .enqueue(communicationWorkRequest)
                }

                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        } else {
            WorkManager
                .getInstance(this)
                .enqueue(communicationWorkRequest)
        }
        lifecycleScope.launch {
            val defconGroup = firebase.firestore.getDefconGroup()
            val c =defconGroup.followers.count()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        optionsMenu = menu
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return when (item.itemId) {
                R.id.action_statusshare -> {
                    shareStatus()
                    true
                }

                R.id.action_listsync -> {
                    lifecycleScope.launch { communication.network.client.requestSyncCheckItems() }
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

                android.R.id.home -> {
                    navController.popBackStack()
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        }
    }

    private fun shareStatus() {
        val bitmap = BitmapUtils.createBitmapFromDrawableResource(
            applicationContext, when (core.preferences.status) {
                1 -> R.drawable.ic_defcon1
                2 -> R.drawable.ic_defcon2
                3 -> R.drawable.ic_defcon3
                4 -> R.drawable.ic_defcon4
                5 -> R.drawable.ic_defcon5
                else -> null
            }
        )
        val uri = BitmapUtils.createUriForBitmap(applicationContext, externalCacheDir, bitmap)
        val intent = BitmapUtils.createImagePngIntent(uri)
        startActivity(Intent.createChooser(intent, "Share with"))
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.navigation_checklist -> {
                optionsMenu?.findItem(R.id.action_listsync)?.isVisible = true
                optionsMenu?.findItem(R.id.action_statusshare)?.isVisible = false
            }

            R.id.navigation_status -> {
                optionsMenu?.findItem(R.id.action_listsync)?.isVisible = false
                optionsMenu?.findItem(R.id.action_statusshare)?.isVisible = true
            }

            else -> {
                optionsMenu?.findItem(R.id.action_listsync)?.isVisible = false
                optionsMenu?.findItem(R.id.action_statusshare)?.isVisible = false
            }
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val view: View? = currentFocus
            if (view is EditText) {
                val outRect = Rect()
                view.getGlobalVisibleRect(outRect)
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