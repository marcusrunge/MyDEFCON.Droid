package com.marcusrunge.mydefcon.firebase.documents

import androidx.annotation.Keep

/**
 * Represents a check item document stored in Firestore.
 *
 * This data class defines the structure of a `CheckItem`.
 *
 * @property id The unique identifier of the check item document.
 * @property uuid The unique identifier of the check item.
 * @property text The text of the check item.
 * @property defcon The DEFCON level of the check item.
 * @property created The timestamp of when the check item was created.
 * @property updated The timestamp of the last modification to this check item.
 */
@Keep
data class CheckItem(
    var id: String = "",
    var uuid: String,
    var text: String?,
    var defcon: Int,
    val created: Long?,
    var updated: Long
)
