package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem

interface Sender {
    suspend fun sendDefconStatus(status: Int)
    suspend fun sendDefconCheckItems(checkItems: List<CheckItem>)
}