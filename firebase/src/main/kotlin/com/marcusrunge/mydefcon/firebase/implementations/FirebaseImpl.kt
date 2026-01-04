package com.marcusrunge.mydefcon.firebase.implementations

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * The concrete implementation of the [com.marcusrunge.mydefcon.firebase.interfaces.Firebase] interface.
 *
 * This internal class provides the actual logic for interacting with Firebase services.
 * It extends [FirebaseBase] to inherit the basic structure and dependencies.
 * Its lifecycle is managed by [com.marcusrunge.mydefcon.firebase.FirebaseFactoryImpl].
 *
 * @param context The application context, required for Firebase initialization.
 * @param core The core component, providing access to shared functionalities.
 */
internal class FirebaseImpl(context: Context?, core: Core?) : FirebaseBase(context, core) {

    private val db = Firebase.firestore

    /**
     * Initializes the Firebase implementation by setting up the concrete implementations
     * for the [firestore] and [realtime] services.
     */
    init {
        _firestore = FirestoreImpl(this)
        _realtime = RealtimeImpl(this)
    }

    /**
     * Asynchronously tests the connection to Firebase Firestore.
     *
     * This function attempts a lightweight read operation on a non-existent document
     * to verify that the client can communicate with the Firestore backend.
     * This is a more reliable way to check connectivity than simply checking host settings.
     *
     * @return `true` if the connection is successful, `false` otherwise.
     */
    override suspend fun testConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Attempt to get a non-existent document to confirm connectivity.
                // The task will fail if the client is offline, effectively testing the connection.
                db.collection("connectivity-test").document("ping").get().await()
                true
            } catch (e: Exception) {
                // Any exception indicates a failure in connecting to the service.
                e.printStackTrace() // Log the exception for debugging purposes.
                false
            }
        }
    }
}