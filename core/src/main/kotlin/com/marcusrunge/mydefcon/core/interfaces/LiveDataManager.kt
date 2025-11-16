package com.marcusrunge.mydefcon.core.interfaces

import kotlinx.coroutines.flow.SharedFlow

interface LiveDataManager {
    val defconStatusFlow: SharedFlow<Pair<Int, String>>
    fun emitDefconStatus(status: Int, source: String)
}