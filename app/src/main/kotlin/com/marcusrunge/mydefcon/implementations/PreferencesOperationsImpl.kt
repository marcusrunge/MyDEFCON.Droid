package com.marcusrunge.mydefcon.implementations

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import javax.inject.Inject

class PreferencesOperationsImpl @Inject constructor(private val context: Context?) :
    PreferencesOperations {
    override fun setInt(key: String, value: Int) {
        TODO("Not yet implemented")
    }

    override fun getInt(key: String): Int {
        TODO("Not yet implemented")
    }

    override fun setBoolean(key: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getBoolean(key: String): Boolean {
        TODO("Not yet implemented")
    }
}