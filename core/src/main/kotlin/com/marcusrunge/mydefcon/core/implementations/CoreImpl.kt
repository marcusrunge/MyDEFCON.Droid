package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

internal class CoreImpl(
    broadcastOperations: BroadcastOperations,
    preferencesOperations: PreferencesOperations

) : CoreBase(preferencesOperations, broadcastOperations) {

    init {
        _coroutineScope = CoroutineScope(Dispatchers.IO + job)
        _broadcast = BroadcastImpl.create(this)
        _defconStatusManager = DefconStatusManagerImpl.create(this)
        _liveDataManager = LiveDataManagerImpl.create(this)
        _preferences = PreferencesImpl.create(this)
    }
}