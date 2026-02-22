package com.marcusrunge.mydefcon.interfaces

/**
 * An interface for synchronizing the checklist.
 *
 * This interface defines a contract for components that need to trigger a
 * synchronization of the checklist data.
 */
interface CheckListSynchronization {
    /**
     * Synchronizes the checklist.
     *
     * This is a suspend function, indicating that it performs a long-running
     * operation, such as fetching data from a remote server or a local database,
     * and should be called from a coroutine.
     */
    suspend fun syncCheckList()
}
