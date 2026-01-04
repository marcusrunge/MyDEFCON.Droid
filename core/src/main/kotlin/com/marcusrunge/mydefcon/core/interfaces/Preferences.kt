package com.marcusrunge.mydefcon.core.interfaces

/**
 * An interface for managing user preferences and application settings.
 *
 * This interface defines a contract for storing and retrieving various preference values
 * that are essential for the application's functionality. These preferences can include
 * user settings, device-specific data, and other configurable options.
 */
interface Preferences {
    /**
     * Gets or sets the current DEFCON status level.
     * This value represents the alert state of the application.
     */
    var status: Int

    /**
     * Gets or sets the ID of the DEFCON group created by the user.
     * This is used to identify the group that the user owns.
     */
    var createdDefconGroupId: String

    /**
     * Gets or sets the ID of the DEFCON group that the user has joined.
     * This is used to identify the group that the user is a member of, but does not own.
     */
    var joinedDefconGroupId: String

    /**
     * Gets or sets a boolean flag indicating whether the user has granted permission
     * for the application to post notifications.
     */
    var isPostNotificationPermissionGranted: Boolean

    /**
     * Gets or sets a boolean flag indicating whether the self-check for post notification
     * permission has been performed. This is used to avoid repeatedly asking for permissions.
     */
    var isPostNotificationSelfPermissionChecked: Boolean
}
