package com.marcusrunge.mydefcon.firebase.interfaces

/**
 * Defines the contract for interacting with the Firebase Realtime Database.
 *
 * This interface will provide methods for managing and synchronizing data in real-time.
 */
interface Realtime {
    /**
     * Fetches the current DEFCON status for a specific group.
     *
     * This function retrieves the current DEFCON status for a group identified by its ID.
     *
     * @param joinedDefconGroupId The ID of the group for which to fetch the status.
     */
    fun fetchDefconStatus(joinedDefconGroupId: String)
}