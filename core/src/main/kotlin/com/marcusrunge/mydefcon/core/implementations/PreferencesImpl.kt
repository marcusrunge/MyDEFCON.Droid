package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Preferences

internal class PreferencesImpl(private val coreBase: CoreBase) : Preferences {

    override var Status: Int
        get() = coreBase.preferencesOperations.getInt("status")
        set(value) {
            coreBase.preferencesOperations.setInt("status", value)
        }

    internal companion object {
        var preferences: Preferences? = null
        fun create(coreBase: CoreBase): Preferences = when {
            preferences != null -> preferences!!
            else -> {
                preferences = PreferencesImpl(coreBase)
                preferences!!
            }
        }
    }
}