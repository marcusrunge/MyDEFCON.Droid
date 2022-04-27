package com.marcusrunge.mydefcon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.marcusrunge.mydefcon.databinding.ActivityMainBinding
import com.marcusrunge.mydefcon.ui.main.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewmodel = viewModel
        binding.lifecycleOwner = this
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            else -> return when (item.itemId) {
                R.id.action_statusshare -> {
                    true
                }
                R.id.action_listsync -> {
                    true
                }
                R.id.navigation_settings -> {
                    true
                }
                R.id.navigation_privacy -> {
                    true
                }
                R.id.navigation_licenses -> {
                    true
                }
                R.id.navigation_terms -> {
                    true
                }
                android.R.id.home -> {
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }
        }
    }
}