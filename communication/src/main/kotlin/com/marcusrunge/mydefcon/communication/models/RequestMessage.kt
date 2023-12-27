package com.marcusrunge.mydefcon.communication.models

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class RequestMessage(val requestCode: Int) {
    val uuid: String = UUID.randomUUID().toString()
}
