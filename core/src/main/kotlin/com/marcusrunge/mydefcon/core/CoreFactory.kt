package com.marcusrunge.mydefcon.core

import com.marcusrunge.mydefcon.core.implementations.CoreImpl
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations

interface CoreFactory {
    /**
     * Creates the core instance
     * @see Core
     */
    fun create(
        preferencesOperations: PreferencesOperations,
        broadcastOperations: BroadcastOperations
    ): Core
}

class CoreFactoryImpl {
    companion object : CoreFactory {
        private var core: Core? = null
        override fun create(
            preferencesOperations: PreferencesOperations,
            broadcastOperations: BroadcastOperations
        ): Core = when {
            core != null -> core!!
            else -> {
                core = CoreImpl(preferencesOperations, broadcastOperations)
                core!!
            }
        }
    }
}