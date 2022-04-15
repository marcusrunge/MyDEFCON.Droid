package com.marcusrunge.mydefcon.utils

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.ui.main.MainViewModel

class BindingUtils {
    object NavViewBindingsAdapter {
        @BindingAdapter("bindNavController")
        @JvmStatic
        fun bindNavController(view: View, mainViewModel: MainViewModel) {
            val navController =
                (view.context as AppCompatActivity).findNavController(R.id.nav_host_fragment_activity_main)
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                )
            )
            (view.context as AppCompatActivity).setupActionBarWithNavController(
                navController,
                appBarConfiguration
            )
            (view as BottomNavigationView).setupWithNavController(navController)
        }
    }
}