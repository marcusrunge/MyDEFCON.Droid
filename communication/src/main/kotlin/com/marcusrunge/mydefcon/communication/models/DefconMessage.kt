package com.marcusrunge.mydefcon.communication.models
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class DefconMessage(val status: Int) {
    val uuid: String = UUID.randomUUID().toString()
}
