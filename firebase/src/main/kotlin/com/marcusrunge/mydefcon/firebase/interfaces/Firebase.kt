package com.marcusrunge.mydefcon.firebase.interfaces

interface Firebase {
    /**
     * Gets the firestore instance.
     * @see Firestore
     */
    val firestore: Firestore

    /**
     * Gets the realtime instance.
     * @see Realtime
     */
    val realtime: Realtime

    /**
     * Test connection to Firestore
     */
    suspend fun testConnection(): Boolean
}