package com.marcusrunge.mydefcon.implementations

import android.content.Context
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import javax.inject.Inject

class PreferencesOperationsImpl @Inject constructor(private val context: Context?) :
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
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String): Boolean {
        TODO("Not yet implemented")
    }
}