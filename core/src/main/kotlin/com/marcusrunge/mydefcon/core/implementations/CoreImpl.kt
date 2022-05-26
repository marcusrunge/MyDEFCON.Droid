package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations

internal class CoreImpl(
    preferencesOperations: PreferencesOperations,
    broadcastOperations: BroadcastOperations
) :
    CoreBase(preferencesOperations, broadcastOperations) {
    init {
        _preferences = PreferencesImpl.create(this)
        _remote = RemoteImpl.create(this)
        _broadcast = BroadcastImpl.create(this)
    }
}