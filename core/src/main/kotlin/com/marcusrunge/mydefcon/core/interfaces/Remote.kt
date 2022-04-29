package com.marcusrunge.mydefcon.core.interfaces

interface Remote {
    /**
     * Shares the defcon status with known devices.
     */
    fun ShareStatus()

    /**
     * Synchonizes the checklist with known devices.
     */
    fun SyncChecklist()
}