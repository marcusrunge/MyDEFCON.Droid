package com.marcusrunge.mydefcon.firebase

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.implementations.FirebaseImpl
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase

/**
 * A factory for creating and managing a singleton instance of the [Firebase] interface.
 * This factory ensures that only one instance of the Firebase services is created and used throughout the application.
 */
interface FirebaseFactory {
    /**
     * Creates or retrieves the singleton instance of [Firebase].
     *
     * @param context The application context.
     * @param core The core component instance.
     * @return The singleton [Firebase] instance.
     * @see Firebase
     */
    fun create(context: Context?, core: Core?): Firebase
}

/**
 * The default implementation of [FirebaseFactory].
 * This class provides a thread-safe singleton instance of [Firebase].
 */
class FirebaseFactoryImpl {
    /**
     * Companion object to hold the factory methods and singleton instance.
     * It implements the [FirebaseFactory] interface to provide the `create` method.
     */
    companion object : FirebaseFactory {
        @Volatile // Ensures that multiple threads handle the instance variable correctly.
        private var instance: Firebase? = null

        /**
         * Creates or retrieves the singleton instance of [Firebase] in a thread-safe manner.
         * This method uses the double-checked locking pattern to ensure that only one instance is created.
         *
         * @param context The application context, which is required for Firebase initialization.
         * @param core The core component, which may be a dependency for Firebase services.
         * @return The singleton [Firebase] instance.
         */
        override fun create(context: Context?, core: Core?): Firebase {
            // Use the existing instance if it's already created.
            return instance ?: synchronized(this) {
                // If the instance is still null, create a new one inside a synchronized block.
                // This double-checked locking ensures thread safety and performance.
                instance ?: FirebaseImpl(context, core).also {
                    instance = it
                }
            }
        }
    }
}