package com.marcusrunge.mydefcon.firebase.implementations

import android.content.Context
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase

internal class FirebaseImpl(context: Context?): FirebaseBase(context) {
    init {
        _firestore = FirestoreImpl.create(this)
        _messaging = MessagingImpl.create(this)
    }

    internal companion object {
        private var instance: Firebase? = null
        fun create(base: FirebaseBase): Firebase = when {
            instance != null -> instance!!
            else -> {
                instance = FirebaseImpl(base.context)
                instance!!
            }
        }
    }
}