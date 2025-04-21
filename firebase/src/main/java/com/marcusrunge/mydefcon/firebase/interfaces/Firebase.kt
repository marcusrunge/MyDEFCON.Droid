package com.marcusrunge.mydefcon.firebase.interfaces

interface Firebase {
    /**
     * Gets the firestore instance.
     * @see Firestore
     */
    val firestore: Firestore

    /**
     * Gets the messaging instance.
     * @see Messaging
     */
    val messaging: Messaging
}