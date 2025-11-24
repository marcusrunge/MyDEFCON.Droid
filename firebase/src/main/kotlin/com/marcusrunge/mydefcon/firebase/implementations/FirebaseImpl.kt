package com.marcusrunge.mydefcon.firebase.implementations

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException

internal class FirebaseImpl(context: Context?, core: Core?): FirebaseBase(context, core) {
    init {
        _firestore = FirestoreImpl.create(this)
        _realtime = RealtimeImpl.create(this)
    }

    internal companion object {
        private var instance: com.marcusrunge.mydefcon.firebase.interfaces.Firebase? = null
        fun create(base: FirebaseBase): com.marcusrunge.mydefcon.firebase.interfaces.Firebase = when {
            instance != null -> instance!!
            else -> {
                instance = FirebaseImpl(base.context, base.core)
                instance!!
            }
        }
    }

    val db = Firebase.firestore
    override suspend fun testConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                db.firestoreSettings.host.isNotEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
                when (e) {
                    is ConnectException -> false
                    is SocketTimeoutException -> false
                    else -> false
                }
            }
        }
    }
}