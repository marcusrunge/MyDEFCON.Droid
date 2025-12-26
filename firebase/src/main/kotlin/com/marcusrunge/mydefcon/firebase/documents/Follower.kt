package com.marcusrunge.mydefcon.firebase.documents

data class Follower(
    var id: String = "",
    var installationId: String = "",
    var isActive: Boolean = false,
    var timestamp: Long = 0,
)