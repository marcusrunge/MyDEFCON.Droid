package com.marcusrunge.mydefcon.core

import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import com.marcusrunge.mydefcon.core.implementations.CoreImpl

interface CoreFactory {
    /**
     * Creates the core instance
     * @see Core
     */
    fun create(preferencesOperations: PreferencesOperations): Core
}

class CoreFactoryImpl {
    companion object : CoreFactory {
        private var core: Core? = null
        override fun create(preferencesOperations: PreferencesOperations): Core = when {
            core != null -> core!!
            else -> {
                core = CoreImpl(preferencesOperations)
                core!!
            }
        }
    }
}