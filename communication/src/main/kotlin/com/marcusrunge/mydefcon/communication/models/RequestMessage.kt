package com.marcusrunge.mydefcon.communication.models

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RequestMessage(val requestCode: Int) {
    val uuid: String = UUID.randomUUID().toString()
}
