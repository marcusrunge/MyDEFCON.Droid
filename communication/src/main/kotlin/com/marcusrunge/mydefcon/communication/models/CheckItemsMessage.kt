package com.marcusrunge.mydefcon.communication.models
import com.marcusrunge.mydefcon.data.entities.CheckItem
import java.util.*
import kotlinx.serialization.Serializable

@Serializable
data class CheckItemsMessage(val checkItems: List<CheckItem>) {
    val uuid: String = UUID.randomUUID().toString()
}
