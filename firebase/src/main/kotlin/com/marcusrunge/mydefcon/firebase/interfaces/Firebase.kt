package com.marcusrunge.mydefcon.firebase.interfaces

/**
 * Defines the contract for accessing Firebase services within the application.
 *
 * This interface provides a centralized and abstracted way to interact with various
 * Firebase features, such as [Firestore] and the [Realtime] Database. It also includes
 * utility functions for managing the Firebase connection.
 */
interface Firebase {
    /**
     * Provides access to the [Firestore] database service.
     *
     * Through this property, you can perform Firestore operations, such as reading, writing,
     * and querying data.
     *
     * @see Firestore
     */
    val firestore: Firestore

    /**
     * Provides access to the [Realtime] Database service.
     *
     * This property allows for interaction with the Firebase Realtime Database,
     * enabling real-time data synchronization.
     *
     * @see Realtime
     */
    val realtime: Realtime

    /**
     * Asynchronously tests the connection to Firebase services.
     *
     * This function can be used to verify that the application can successfully
     * communicate with Firebase, which is useful for diagnostics and ensuring
     * that the services are configured correctly.
     *
     * @return `true` if the connection is successful, `false` otherwise.
     */
    suspend fun testConnection(): Boolean
}