package com.marcusrunge.mydefcon.data.interfaces

interface Data {
    /**
     * Gets the repository instance.
     * @see Repository
     */
    val repository: Repository

    /**
     * Gets the firestore instance.
     * @see Firestore
     */
    val firestore: Firestore
}