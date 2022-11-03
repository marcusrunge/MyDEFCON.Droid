package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Preferences
import java.util.concurrent.atomic.AtomicInteger

internal class PreferencesImpl(private val coreBase: CoreBase) : Preferences {
    private val _status = AtomicInteger()

    init {
        _status.set(coreBase.preferencesOperations.getInt("status"))
    }

    override var status: Int = _status.get()
        set(value) {
            _status.set(value)
            coreBase.preferencesOperations.setInt("status", value)
            //Send to MyDefconWidget.
            coreBase.broadcastOperations.sendBroadcast(
                "com.marcusrunge.mydefcon.DEFCON_UPDATE",
                value.toString()
            )
        }

    internal companion object {
        private var preferences: Preferences? = null
        fun create(coreBase: CoreBase): Preferences = when {
            preferences != null -> preferences!!
            else -> {
                preferences = PreferencesImpl(coreBase)
                preferences!!
            }
        }
    }
}