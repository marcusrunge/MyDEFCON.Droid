package com.marcusrunge.mydefcon.communication.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class DefconMessage(val status: Int) {
    val uuid: String = UUID.randomUUID().toString()
}
