package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.LiveDataManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

internal class LiveDataManagerImpl(private val coreBase: CoreBase) : LiveDataManager {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val _defconStatusFlow = MutableSharedFlow<Pair<Int, Class<*>>>()
    override val defconStatusFlow: SharedFlow<Pair<Int, Class<*>>>
        get() =_defconStatusFlow.asSharedFlow()

    override fun emitDefconStatus(status: Int, source: Class<*>) {
        scope.launch {
            _defconStatusFlow.emit( Pair(status, source))
        }
    }

    internal companion object {
        private var liveDataManager: LiveDataManager? = null
        fun create(coreBase: CoreBase): LiveDataManager = when {
            liveDataManager != null -> liveDataManager!!
            else -> {
                liveDataManager = LiveDataManagerImpl(coreBase)
                liveDataManager!!
            }
        }
    }
}