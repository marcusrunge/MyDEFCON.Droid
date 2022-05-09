package com.marcusrunge.mydefcon.core.implementations

import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Checklist

internal class ChecklistImpl(private val coreBase: CoreBase): Checklist {
    internal companion object {
        var checklistOperations: Checklist? = null
        fun create(coreBase: CoreBase): Checklist = when {
            checklistOperations != null -> checklistOperations!!
            else -> {
                checklistOperations = ChecklistImpl(coreBase)
                checklistOperations!!
            }
        }
    }
}