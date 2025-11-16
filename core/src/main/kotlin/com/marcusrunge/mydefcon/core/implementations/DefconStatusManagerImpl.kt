package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.DefconStatusManager
import kotlinx.coroutines.launch

internal class DefconStatusManagerImpl(private val coreBase: CoreBase) : DefconStatusManager {
    private var isInitialized: Boolean = false

    init {
        initialize()
    }

    override fun initialize() {
        if (!isInitialized) {
            coreBase.coroutineScope?.launch {
                coreBase.liveDataManager?.defconStatusFlow?.collect { pair ->
                    coreBase.broadcastOperations.sendBroadcast(
                        "com.marcusrunge.mydefcon.DEFCON_UPDATE",
                        pair.first.toString(),
                        pair.second
                    )
                }
            }
            isInitialized = true
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
