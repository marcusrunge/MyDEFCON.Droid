package com.marcusrunge.mydefcon.implementations

import android.content.Context
import androidx.core.content.edit
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import javax.inject.Inject

/**
 * Implements [PreferencesOperations] for managing shared preferences.
 *
 * This class provides a concrete implementation for storing and retrieving key-value pairs
 * using Android's `SharedPreferences`. It's designed to be injected via Hilt.
 *
 * @param context The application context, used to access `SharedPreferences`.
 */
internal class PreferencesOperationsImpl @Inject constructor(context: Context?) :
    PreferencesOperations {

    /**
     * The `SharedPreferences` instance for the application.
     *
     * It's initialized using the application context to get the preferences file named
     * according to `R.string.sharedpreferences_name`.
     */
    private val sharedPref = context?.run {
        getSharedPreferences(getString(R.string.sharedpreferences_name), Context.MODE_PRIVATE)
    }

    /**
     * Stores an integer value in shared preferences.
     *
     * This function uses the [edit] extension function for a concise, block-based approach
     * to editing `SharedPreferences`. The changes are applied atomically.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    override fun setInt(key: String, value: Int) {
        sharedPref?.edit {
            putInt(key, value)
        }
    }

    /**
     * Retrieves an integer value from shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return The preference value if it exists, or 0 if it does not.
     */
    override fun getInt(key: String): Int {
        return sharedPref?.getInt(key, 0) ?: 0
    }

    /**
     * Stores a boolean value in shared preferences.
     *
     * This function uses the [edit] extension function for a concise, block-based approach
     * to editing `SharedPreferences`. The changes are applied atomically.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    override fun setBoolean(key: String, value: Boolean) {
        sharedPref?.edit {
            putBoolean(key, value)
        }
    }

    /**
     * Retrieves a boolean value from shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return The preference value if it exists, or false if it does not.
     */
    override fun getBoolean(key: String): Boolean {
        return sharedPref?.getBoolean(key, false) ?: false
    }

    /**
     * Stores a string value in shared preferences.
     *
     * This function uses the [edit] extension function for a concise, block-based approach
     * to editing `SharedPreferences`. The changes are applied atomically.
     *
     * @param key The name of the preference to modify.
     * @param value The new value for the preference.
     */
    override fun setString(key: String, value: String) {
        sharedPref?.edit {
            putString(key, value)
        }
    }

    /**
     * Retrieves a string value from shared preferences.
     *
     * @param key The name of the preference to retrieve.
     * @return The preference value if it exists, or an empty string if it does not.
     */
    override fun getString(key: String): String {
        return sharedPref?.getString(key, "") ?: ""
    }
}
