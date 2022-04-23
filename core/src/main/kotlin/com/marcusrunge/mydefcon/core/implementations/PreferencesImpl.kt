package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.interfaces.Preferences
import com.marcusrunge.mydefcon.core.bases.CoreBase

internal class PreferencesImpl(private val coreBase: CoreBase) : Preferences {

    override var status: Int
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