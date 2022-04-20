package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.interfaces.PreferencesOperations
import com.marcusrunge.mydefcon.core.bases.CoreBase

internal class CoreImpl(preferencesOperations: PreferencesOperations) :
    CoreBase(preferencesOperations) {
    init {
        _preferences = PreferencesImpl.create(this)
    }
}