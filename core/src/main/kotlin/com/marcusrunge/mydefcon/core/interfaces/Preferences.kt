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
}