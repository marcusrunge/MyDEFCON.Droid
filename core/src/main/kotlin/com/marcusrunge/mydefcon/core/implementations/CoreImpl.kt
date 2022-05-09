package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.DatabaseOperations
import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations

internal class CoreImpl(preferencesOperations: PreferencesOperations, databaseOperations: DatabaseOperations) :
    CoreBase(preferencesOperations, databaseOperations) {
    init {
        _preferences = PreferencesImpl.create(this)
        _remote = RemoteImpl.create(this)
        _checklist=ChecklistImpl.create(this)
    }
}