package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.Preferences
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations

internal abstract class CoreBase(private val _preferencesOperations: PreferencesOperations) : Core {
    protected lateinit var _preferences: Preferences
    override val preferences: Preferences
        get() = _preferences

    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations
}