package com.marcusrunge.mydefcon.firebase.interfaces

interface Messaging {
    fun sendDefconStatusToFollower(status:Int, fcmToken:String)
}