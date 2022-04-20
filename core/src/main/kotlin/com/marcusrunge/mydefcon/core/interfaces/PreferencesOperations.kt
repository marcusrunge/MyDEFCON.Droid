package com.marcusrunge.mydefcon.core.interfaces

interface PreferencesOperations {
    fun setInt(key: String, value: Int)
    fun getInt(key: String): Int
    fun setBoolean(key: String, value: Boolean)
    fun getBoolean(key: String): Boolean
}