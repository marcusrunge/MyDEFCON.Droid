package com.marcusrunge.mydefcon.core.interfaces

interface Preferences {
    /**
     * Gets or sets the defcon status.
     */
    var status: Int

    /**
     * Gets or sets the created defcon group id.
     */
    var createdDefconGroupId: String

    /**
     * Gets or sets the joined defcon group id.
     */
    var joinedDefconGroupId: String

    /**
     * Gets or sets whether the post notification permission is granted.
     */
    var isPostNotificationPermissionGranted: Boolean
    /**
     * Gets or sets whether the post notification self permission is checked.
     */
    var isPostNotificationSelfPermissionChecked: Boolean
}