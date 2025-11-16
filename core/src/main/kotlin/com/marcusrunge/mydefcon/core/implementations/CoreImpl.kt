package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations

internal class CoreImpl(
    broadcastOperations: BroadcastOperations,
    preferencesOperations: PreferencesOperations

) : CoreBase(preferencesOperations, broadcastOperations) {

    init {
        _broadcast = BroadcastImpl.create(this)
        _preferences = PreferencesImpl.create(this)
        _liveDataManager = LiveDataManagerImpl.create(this)
    }
}