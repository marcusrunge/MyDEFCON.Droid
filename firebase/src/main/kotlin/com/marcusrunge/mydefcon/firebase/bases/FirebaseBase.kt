package com.marcusrunge.mydefcon.firebase.bases

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore
import com.marcusrunge.mydefcon.firebase.interfaces.Realtime

/**
 * An abstract base class that provides a foundational implementation of the [Firebase] interface.
 *
 * This class is designed to be extended by concrete implementations that provide specific
 * Firebase functionalities. It manages the dependencies on [Context] and [Core] and

 * provides protected properties for [Firestore] and [Realtime] database services,
 * which must be initialized by subclasses.
 *
 * @property context The application context, used for initializing Firebase services.
 * @property core The core component, providing access to shared functionalities.
 */
internal abstract class FirebaseBase(
    internal val context: Context?,
    internal val core: Core?
) : Firebase {

    /**
     * The backing property for the [Firestore] service.
     * It must be initialized by a subclass.
     */
    protected lateinit var _firestore: Firestore

    /**
     * The backing property for the [Realtime] database service.
     * It must be initialized by a subclass.
     */
    protected lateinit var _realtime: Realtime

    /**
     * Provides access to the [Firestore] service.
     * @return The initialized [Firestore] instance.
     * @throws UninitializedPropertyAccessException if `_firestore` has not been initialized.
     */
    override val firestore: Firestore
        get() = _firestore

    /**
     * Provides access to the [Realtime] database service.
     * @return The initialized [Realtime] instance.
     * @throws UninitializedPropertyAccessException if `_realtime` has not been initialized.
     */
    override val realtime: Realtime
        get() = _realtime
}