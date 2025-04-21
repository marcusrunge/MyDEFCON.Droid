package com.marcusrunge.mydefcon.core.interfaces

interface Preferences {
    /**
     * Gets or sets the defcon status.
     */
    var status: Int

    /**
     * Gets or sets the FCM registration token.
     */
    var fcmRegistrationToken: String
}