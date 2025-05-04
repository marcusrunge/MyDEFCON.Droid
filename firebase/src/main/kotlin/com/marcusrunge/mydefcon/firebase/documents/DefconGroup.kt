package com.marcusrunge.mydefcon.firebase.documents

data class DefconGroup(
    var id: String = "",
    var leader: String = "",
    var followers: MutableList<Follower> = emptyList<Follower>().toMutableList(),
    var timestamp: Long = 0,
)