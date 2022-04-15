package com.marcusrunge.mydefcon.core

import com.marcusrunge.mydefcon.core.Interfaces.Core
import com.marcusrunge.mydefcon.core.implementations.CoreImpl

interface CoreFactory {
    /**
     * Creates the core instance
     * @see Core
     */
    fun create(): Core
}

class CoreFactoryImpl {
    companion object : CoreFactory {
        private var core: Core? = null
        override fun create(): Core = when {
            core != null -> core!!
            else -> {
                core = CoreImpl()
                core!!
            }
        }
    }
}