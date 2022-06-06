package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.*

internal abstract class CoreBase(
    private val _preferencesOperations: PreferencesOperations,
    private val _broadcastOperations: BroadcastOperations
) : Core {
    protected lateinit var _preferences: Preferences
    protected lateinit var _broadcast: Broadcast
    override val preferences: Preferences
        get() = _preferences
    override val broadCast: Broadcast
        get() = _broadcast
    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations
    internal val broadcastOperations: BroadcastOperations get() = _broadcastOperations
}