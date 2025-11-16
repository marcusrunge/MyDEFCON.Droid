package com.marcusrunge.mydefcon.core.interfaces

import kotlinx.coroutines.flow.SharedFlow

interface LiveDataManager {
    val defconStatusFlow: SharedFlow<Pair<Int, Class<*>>>
    fun emitDefconStatus(status: Int, source: Class<*>)
}