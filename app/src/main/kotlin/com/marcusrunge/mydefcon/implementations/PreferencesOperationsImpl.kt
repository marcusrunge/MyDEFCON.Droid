package com.marcusrunge.mydefcon.implementations

import android.content.Context
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import javax.inject.Inject

internal class PreferencesOperationsImpl @Inject constructor(context: Context?) :
    PreferencesOperations {
    private val sharedPref = context?.getSharedPreferences(
        context.getString(R.string.sharedpreferences_name),
        Context.MODE_PRIVATE
    )

    override fun setInt(key: String, value: Int) {
        sharedPref?.edit()?.putInt(key, value)?.apply()
    }

    override fun getInt(key: String): Int {
        return sharedPref?.getInt(key, 0)!!
    }

    override fun setBoolean(key: String, value: Boolean) {
        sharedPref?.edit()?.putBoolean(key, value)?.apply()
    }

    override fun getBoolean(key: String): Boolean {
        return sharedPref?.getBoolean(key, false)!!
    }

    override fun setString(key: String, value: String) {
        sharedPref?.edit()?.putString(key, value)?.apply()
    }

    override fun getString(key: String): String {
        return sharedPref?.getString(key, null)!!
    }
}