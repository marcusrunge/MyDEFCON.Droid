package com.marcusrunge.mydefcon.core.interfaces

/**
 * An interface for managing the DEFCON status of the application.
 *
 * This interface defines the contract for initializing and managing the logic related to
 * the DEFCON status.
 */
interface DefconStatusManager {
    /**
     * Initializes the DEFCON status manager.
     *
     * This function should be called to set up any necessary listeners or initial states
     * required for managing the DEFCON status.
     */
    fun initialize()
}
