package com.marcusrunge.mydefcon.communication.models

import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class CheckItemsMessage(val checkItems: List<CheckItem>) {
    val uuid: String = UUID.randomUUID().toString()
}
