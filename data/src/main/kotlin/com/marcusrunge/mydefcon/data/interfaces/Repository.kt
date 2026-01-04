package com.marcusrunge.mydefcon.data.interfaces

/**
 * Defines the contract for the main repository of the application.
 *
 * This interface provides access to various Data Access Objects (DAOs) that are used
 * to interact with the underlying data sources, such as the database. It serves as an
 * abstraction layer, decoupling the data access logic from the rest of the application.
 */
interface Repository {
    /**
     * The Data Access Object for check items.
     *
     * This property provides access to the [CheckItems], which contains methods
     * for querying, inserting, updating, and deleting check items from the database.
     *
     * @see CheckItems
     */
    val checkItems: CheckItems
}