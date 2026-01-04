package com.marcusrunge.mydefcon.core.interfaces

/**
 * An interface for performing low-level operations on the application's preferences.
 *
 * This interface defines a contract for getting and setting primitive data types
 * in a persistent key-value store. It serves as an abstraction over the underlying
 * preferences implementation (e.g., SharedPreferences).
 */
interface PreferencesOperations {
    /**
     * Stores an integer value for the given key.
     *
     * @param key The key under which the value is stored.
     * @param value The integer value to store.
     */
    fun setInt(key: String, value: Int)

    /**
     * Retrieves an integer value for the given key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The integer value, or a default value (e.g., 0) if the key is not found.
     */
    fun getInt(key: String): Int

    /**
     * Stores a boolean value for the given key.
     *
     * @param key The key under which the value is stored.
     * @param value The boolean value to store.
     */
    fun setBoolean(key: String, value: Boolean)

    /**
     * Retrieves a boolean value for the given key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The boolean value, or a default value (e.g., false) if the key is not found.
     */
    fun getBoolean(key: String): Boolean

    /**
     * Stores a string value for the given key.
     *
     * @param key The key under which the value is stored.
     * @param value The string value to store.
     */
    fun setString(key: String, value: String)

    /**
     * Retrieves a string value for the given key.
     *
     * @param key The key whose associated value is to be returned.
     * @return The string value, or a default value (e.g., an empty string) if the key is not found.
     */
    fun getString(key: String): String
}
