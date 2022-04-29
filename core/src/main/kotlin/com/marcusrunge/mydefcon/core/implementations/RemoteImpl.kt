package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Remote

internal class RemoteImpl(private val coreBase: CoreBase) : Remote {

    override fun ShareStatus() {
        TODO("Not yet implemented")
    }

    override fun SyncChecklist() {
        TODO("Not yet implemented")
    }

    internal companion object {
        var remote: Remote? = null
        fun create(coreBase: CoreBase): Remote = when {
            remote != null -> remote!!
            else -> {
                remote = RemoteImpl(coreBase)
                remote!!
            }
        }
    }
}