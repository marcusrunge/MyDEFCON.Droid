package com.marcusrunge.mydefcon.data.implementations

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.interfaces.Firestore

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
}