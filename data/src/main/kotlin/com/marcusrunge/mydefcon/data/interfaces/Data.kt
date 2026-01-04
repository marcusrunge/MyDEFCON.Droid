package com.marcusrunge.mydefcon.data.interfaces

/**
 * Defines the primary entry point for the data layer of the application.
 *
 * This interface provides access to the application's data sources by exposing repository
 * instances. It acts as a contract for the data layer, abstracting the underlying
 * data management logic from other parts of the application, such as the UI layer.
 *
 * Implementations of this interface are responsible for initializing and providing
 * the necessary repositories.
 */
interface Data {
    /**
     * The main repository for accessing and managing application data.
     *
     * This repository provides a unified interface for various data operations,
     * abstracting the specific data sources (e.g., database, network).
     *
     * @see Repository
     */
    val repository: Repository
}
