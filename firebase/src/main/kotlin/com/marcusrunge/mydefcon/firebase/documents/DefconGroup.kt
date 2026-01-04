package com.marcusrunge.mydefcon.firebase.documents

/**
 * Represents a DEFCON group document stored in Firestore.
 *
 * This data class defines the structure of a `DefconGroup`, including its unique identifier,
 * the leader's installation ID, a list of followers, and the timestamp of the last update.
 *
 * @property id The unique identifier of the DEFCON group document.
 * @property leader The installation ID of the user who leads the group.
 * @property followers A mutable list of [Follower] objects who are part of this group.
 * @property timestamp The timestamp of the last modification to this group, typically in milliseconds.
 */
data class DefconGroup(
    var id: String = "",
    var leader: String = "",
    var followers: MutableList<Follower> = mutableListOf(),
    var timestamp: Long = 0,
)
