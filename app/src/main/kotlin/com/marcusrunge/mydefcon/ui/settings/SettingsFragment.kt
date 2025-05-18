package com.marcusrunge.mydefcon.ui.settings

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.marcusrunge.mydefcon.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val createButtonPreference: Preference? = findPreference("create_button")
        val joinButtonPreference: Preference? = findPreference("join_button")
        createButtonPreference?.setOnPreferenceClickListener {
            Log.d("SettingsFragment", "Create Button Clicked!")
            Toast.makeText(context, "Create Button Clicked", Toast.LENGTH_SHORT).show()
            true
        }
        joinButtonPreference?.setOnPreferenceClickListener {
            Log.d("SettingsFragment", "Join Button Clicked!")
            Toast.makeText(context, "Join Button Clicked", Toast.LENGTH_SHORT).show()
            true
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

    override fun onSharedPreferenceChanged(
        sharedPreferences: SharedPreferences?,
        key: String?
    ) {
        TODO("Not yet implemented")
    }
}