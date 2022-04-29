package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.Preferences
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import com.marcusrunge.mydefcon.core.interfaces.Remote

internal abstract class CoreBase(private val _preferencesOperations: PreferencesOperations) : Core {
    protected lateinit var _preferences: Preferences
    protected lateinit var _remote: Remote
    override val preferences: Preferences
        get() = _preferences
    override val remote: Remote
        get() = _remote

    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations
}