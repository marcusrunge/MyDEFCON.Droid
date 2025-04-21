package com.marcusrunge.mydefcon.firebase.bases

import android.content.Context
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore
import com.marcusrunge.mydefcon.firebase.interfaces.Messaging

internal abstract class FirebaseBase(internal val context: Context?) : Firebase {
    protected lateinit var _firestore: Firestore
    protected lateinit var _messaging: Messaging

    override val firestore: Firestore
        get() = _firestore
    override val messaging: Messaging
        get() = _messaging
}