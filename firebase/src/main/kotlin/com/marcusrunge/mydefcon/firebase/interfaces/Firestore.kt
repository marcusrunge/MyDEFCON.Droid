package com.marcusrunge.mydefcon.firebase.interfaces

import com.marcusrunge.mydefcon.firebase.documents.DefconGroup
import com.marcusrunge.mydefcon.firebase.documents.Follower

interface Firestore {
    suspend fun getDefconGroup(documentId: String): DefconGroup
    suspend fun createDefconGroup(): String
    suspend fun deleteDefconGroup(documentId: String)
    suspend fun joinDefconGroup(documentId: String, installationId: String)
    suspend fun leaveDefconGroup(documentId: String, installationId: String)
    suspend fun checkIfDefconGroupExists(documentId: String): Boolean
    suspend fun checkIfFollowerInDefconGroupExists(
        documentId: String,
        installationId: String
    ): Boolean
    suspend fun getDefconGroupFollowers(documentId: String): List<Follower>
}