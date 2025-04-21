package com.marcusrunge.mydefcon.firebase.implementations

import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore



internal class FirestoreImpl(private val base: FirebaseBase) : Firestore {
    internal companion object {
        private var instance: Firestore? = null
        fun create(base: FirebaseBase): Firestore = when {
            instance != null -> instance!!
            else -> {
                instance = FirestoreImpl(base)
                instance!!
            }
        }
    }
}