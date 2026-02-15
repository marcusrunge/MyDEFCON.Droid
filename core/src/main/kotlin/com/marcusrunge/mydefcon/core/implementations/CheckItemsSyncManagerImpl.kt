package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.CheckItemsSyncManager

internal class CheckItemsSyncManagerImpl(private val coreBase: CoreBase) : CheckItemsSyncManager {
    internal companion object {
        @Volatile
        private var instance: CheckItemsSyncManager? = null

        fun create(coreBase: CoreBase): CheckItemsSyncManager =
            instance ?: synchronized(this) {
                instance ?: CheckItemsSyncManagerImpl(coreBase).also { instance = it }
            }
    }
}