package com.marcusrunge.mydefcon.data.interfaces

interface Data {
    /**
     * Gets the repository instance
     * @see Repository
     */
    val repository: Repository

    /**
     * Gets the settings instance
     * @see Settings
     */
    val settings: Settings
}