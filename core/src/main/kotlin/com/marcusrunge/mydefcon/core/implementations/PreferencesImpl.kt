package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Preferences

/**
 * An implementation of the [Preferences] interface.
 *
 * This class provides a concrete implementation for storing and retrieving application
 * preferences. It acts as a wrapper around the [PreferencesOperations] provided by
 * [CoreBase], mapping high-level preference properties to low-level key-value storage.
 *
 * It is implemented as a singleton to ensure that all parts of the application access the
 * same preference store.
 *
 * @param coreBase The core base dependency providing access to low-level preference operations.
 */
internal class PreferencesImpl(private val coreBase: CoreBase) : Preferences {

    /**
     * Gets or sets the current DEFCON status level.
     * The value is stored persistently under the key "status".
     */
    override var status: Int
        get() = coreBase.preferencesOperations.getInt("status")
        set(value) {
            coreBase.preferencesOperations.setInt("status", value)
        }

    /**
     * Gets or sets the ID of the DEFCON group created by the user.
     * The value is stored persistently under the key "createdDefconGroupId".
     */
    override var createdDefconGroupId: String
        get() = coreBase.preferencesOperations.getString("createdDefconGroupId")
        set(value) {
            coreBase.preferencesOperations.setString("createdDefconGroupId", value)
        }

    /**
     * Gets or sets the ID of the DEFCON group that the user has joined.
     * The value is stored persistently under the key "joinedDefconGroupId".
     */
    override var joinedDefconGroupId: String
        get() = coreBase.preferencesOperations.getString("joinedDefconGroupId")
        set(value) {
            coreBase.preferencesOperations.setString("joinedDefconGroupId", value)
        }

    /**
     * Gets or sets a boolean flag indicating whether the user has granted permission
     * for the application to post notifications.
     * The value is stored persistently under the key "isPostNotificationPermissionGranted".
     */
    override var isPostNotificationPermissionGranted: Boolean
        get() = coreBase.preferencesOperations.getBoolean("isPostNotificationPermissionGranted")
        set(value) {
            coreBase.preferencesOperations.setBoolean("isPostNotificationPermissionGranted", value)
        }

    /**
     * Gets or sets a boolean flag indicating whether the self-check for post notification
     * permission has been performed.
     * The value is stored persistently under the key "isPostNotificationSelfPermissionChecked".
     */
    override var isPostNotificationSelfPermissionChecked: Boolean
        get() = coreBase.preferencesOperations.getBoolean("isPostNotificationSelfPermissionChecked")
        set(value) {
            coreBase.preferencesOperations.setBoolean("isPostNotificationSelfPermissionChecked", value)
        }

    internal companion object {
        @Volatile
        private var instance: Preferences? = null

        /**
         * Creates and returns a singleton instance of the [Preferences] interface.
         *
         * This function ensures that only one instance of [PreferencesImpl] is created
         * and used throughout the application. It uses a thread-safe, double-checked locking
         * mechanism for robust singleton implementation.
         *
         * @param coreBase The [CoreBase] instance required for the [PreferencesImpl] constructor.
         * @return The singleton instance of [Preferences].
         */
        fun create(coreBase: CoreBase): Preferences =
            instance ?: synchronized(this) {
                instance ?: PreferencesImpl(coreBase).also { instance = it }
            }
    }
}
