package com.marcusrunge.mydefcon.firebase.bases

import android.content.Context
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore
import com.marcusrunge.mydefcon.firebase.interfaces.Realtime

internal abstract class FirebaseBase(internal val context: Context?, internal val core: Core?) : Firebase {
    protected lateinit var _firestore: Firestore
    protected lateinit var _realtime: Realtime

    override val firestore: Firestore
        get() = _firestore
    override val realtime: Realtime
        get() = _realtime
}