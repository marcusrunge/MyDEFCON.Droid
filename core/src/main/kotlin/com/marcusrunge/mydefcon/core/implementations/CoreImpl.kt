package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations

internal class CoreImpl(preferencesOperations: PreferencesOperations) :
    CoreBase(preferencesOperations) {
    init {
        _preferences = PreferencesImpl.create(this)
        _remote = RemoteImpl.create(this)
        _checklist=ChecklistImpl.create(this)
    }
}