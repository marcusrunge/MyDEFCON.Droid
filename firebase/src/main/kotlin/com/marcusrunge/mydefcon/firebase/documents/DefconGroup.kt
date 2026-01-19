package com.marcusrunge.mydefcon.firebase.documents

import androidx.annotation.Keep

/**
 * Represents a DEFCON group document stored in Firestore.
 *
 * This data class defines the structure of a `DefconGroup`, including its unique identifier,
 * the leader's installation ID, a list of followers, and the timestamp of the last update.
 *
 * @property checkItems A mutable list of [CheckItem] objects.
 * @property followers A mutable list of [Follower] objects who are part of this group.
 * @property id The unique identifier of the DEFCON group document.
 * @property leader The installation ID of the user who leads the group.
 * @property timestamp The timestamp of the last modification to this group, typically in milliseconds.
 */
@Keep
data class DefconGroup(
    var checkItems: MutableList<CheckItem> = mutableListOf(),
    var followers: MutableList<Follower> = mutableListOf(),
    var id: String = "",
    var leader: String = "",
    var timestamp: Long = 0,
)
