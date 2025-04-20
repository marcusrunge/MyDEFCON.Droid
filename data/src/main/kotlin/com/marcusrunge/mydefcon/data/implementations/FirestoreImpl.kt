package com.marcusrunge.mydefcon.data.implementations

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.interfaces.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.SocketTimeoutException

internal class FirestoreImpl(dataBase: DataBase) : Firestore {
    companion object {
        private var firestore: Firestore? = null
        fun create(dataBase: DataBase): Firestore = when {
            firestore != null -> firestore!!
            else -> {
                firestore = FirestoreImpl(dataBase)
                firestore!!
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