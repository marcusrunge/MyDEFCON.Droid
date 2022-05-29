package com.marcusrunge.mydefcon.communication.interfaces

import com.marcusrunge.mydefcon.data.entities.CheckItem

interface Sender {
    fun sendDefconStatus(status: Int)
    fun sendDefconCheckItems(checkItems: List<CheckItem>)
}