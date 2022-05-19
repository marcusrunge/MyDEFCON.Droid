package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.*

internal abstract class CoreBase(private val _preferencesOperations: PreferencesOperations) : Core {
    protected lateinit var _preferences: Preferences
    protected lateinit var _remote: Remote
    protected lateinit var _checklist:Checklist
    override val preferences: Preferences
        get() = _preferences
    override val remote: Remote
        get() = _remote
    override val checklist: Checklist
        get() = _checklist

    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations
}