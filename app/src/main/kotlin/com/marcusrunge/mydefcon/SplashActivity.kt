package com.marcusrunge.mydefcon

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.marcusrunge.mydefcon.databinding.ActivitySplashBinding
import com.marcusrunge.mydefcon.ui.splash.SplashViewModel

/**
 * An activity that shows a splash screen on app startup.
 * After a short delay, it navigates to the [MainActivity].
 */
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val viewModel by viewModels<SplashViewModel>()

    // The duration of the splash screen in milliseconds.
    private val SPLASH_TIME_OUT: Long = 1000

    /**
     * Initializes the activity, sets up data binding, and starts a timer
     * to transition to the main activity after a delay.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in [onSaveInstanceState].  <b><i>Note: Otherwise it is null.</i></b>
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate layout and set up data binding
        binding = ActivitySplashBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)

        // Use a Handler to delay the start of the MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // Create an Intent to start the MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            // Finish this activity to prevent the user from navigating back to it
            finish()
        }, SPLASH_TIME_OUT)
    }
}
