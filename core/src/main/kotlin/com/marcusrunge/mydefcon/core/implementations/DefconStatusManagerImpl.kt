package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.DefconStatusManager
import kotlinx.coroutines.launch

internal class DefconStatusManagerImpl(private val coreBase: CoreBase) : DefconStatusManager {

    override fun initialize() {
        coreBase.coroutineScope?.launch {
            coreBase.liveDataManager?.defconStatusFlow?.collect { pair ->
                coreBase.broadCast?.sendDefconBroadcast(
                    pair.first,
                    pair.second
                )
            }
        }
    }

    internal companion object {
        private var defconStatusManager: DefconStatusManager? = null
        fun create(coreBase: CoreBase): DefconStatusManager = when {
            defconStatusManager != null -> defconStatusManager!!
            else -> {
                defconStatusManager = DefconStatusManagerImpl(coreBase)
                defconStatusManager!!
            }
        }
    }
}
