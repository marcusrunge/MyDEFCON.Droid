package com.marcusrunge.mydefcon.communication.models
import java.util.*
data class DefconMessage(val status: Int) {
    val uuid: String = UUID.randomUUID().toString()
}
