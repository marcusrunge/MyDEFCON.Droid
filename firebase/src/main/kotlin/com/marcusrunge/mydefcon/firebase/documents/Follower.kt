package com.marcusrunge.mydefcon.firebase.documents

/**
 * Represents a follower document within a DEFCON group in Firestore.
 *
 * This data class defines the structure of a `Follower`, including its unique identifier,
 * the installation ID, its active status, and the timestamp of the last update.
 *
 * @property id The unique identifier of the follower document.
 * @property installationId The unique installation ID of the follower's device.
 * @property isActive A boolean indicating whether the follower is currently active in the group.
 * @property timestamp The timestamp of the last modification to this follower's status, typically in milliseconds.
 */
data class Follower(
    var id: String = "",
    var installationId: String = "",
    var isActive: Boolean = false,
    var timestamp: Long = 0,
)
