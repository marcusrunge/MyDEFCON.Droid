package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.*

internal abstract class CoreBase(private val _preferencesOperations: PreferencesOperations) : Core {
    protected lateinit var _preferences: Preferences
    protected lateinit var _remote: Remote
    override val preferences: Preferences
        get() = _preferences
    override val remote: Remote
        get() = _remote

    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations
}