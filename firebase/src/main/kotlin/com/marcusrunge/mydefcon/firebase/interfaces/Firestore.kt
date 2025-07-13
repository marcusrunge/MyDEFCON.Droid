package com.marcusrunge.mydefcon.firebase.interfaces

import com.marcusrunge.mydefcon.firebase.documents.DefconGroup

interface Firestore {
    suspend fun getDefconGroup(documentId: String): DefconGroup
    suspend fun createDefconGroup(): String
    suspend fun deleteDefconGroup(documentId: String)
    suspend fun joinDefconGroup(documentId: String, fcmToken: String)
    suspend fun leaveDefconGroup(documentId: String, fcmToken: String)
}