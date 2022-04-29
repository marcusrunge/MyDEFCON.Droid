package com.marcusrunge.mydefcon.core.interfaces

interface PreferencesOperations {
    /**
     * Sets a key / value pair.
     * @param key the key.
     * @param value the value.
     */
    fun setInt(key: String, value: Int)

    /**
     * Gets a value pair.
     * @param key the key.
     * @return the value.
     */
    fun getInt(key: String): Int

    /**
     * Sets a key / value pair.
     * @param key the key.
     * @param value the value.
     */
    fun setBoolean(key: String, value: Boolean)

    /**
     * Gets a value pair.
     * @param key the key.
     * @return the value.
     */
    fun getBoolean(key: String): Boolean
}