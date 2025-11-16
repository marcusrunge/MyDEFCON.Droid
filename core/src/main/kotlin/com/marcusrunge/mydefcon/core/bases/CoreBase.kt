package com.marcusrunge.mydefcon.core.bases

import com.marcusrunge.mydefcon.core.interfaces.Broadcast
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.core.interfaces.DefconStatusManager
import com.marcusrunge.mydefcon.core.interfaces.LiveDataManager
import com.marcusrunge.mydefcon.core.interfaces.Preferences
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

internal abstract class CoreBase(
    private val _preferencesOperations: PreferencesOperations,
    private val _broadcastOperations: BroadcastOperations
) : Core {
    protected lateinit var _preferences: Preferences
    protected lateinit var _broadcast: Broadcast
    protected lateinit var _liveDataManager: LiveDataManager
    protected lateinit var _defconStatusManager: DefconStatusManager
    protected lateinit var _coroutineScope: CoroutineScope

    protected val job = SupervisorJob()




    override val preferences: Preferences?
        get() = _preferences
    override val broadCast: Broadcast?
        get() = _broadcast
    override val liveDataManager: LiveDataManager?
        get() = _liveDataManager
    override val defconStatusManager: DefconStatusManager?
        get() = _defconStatusManager
    override val coroutineScope: CoroutineScope?
        get() = _coroutineScope
    internal val preferencesOperations: PreferencesOperations get() = _preferencesOperations
    internal val broadcastOperations: BroadcastOperations get() = _broadcastOperations
}