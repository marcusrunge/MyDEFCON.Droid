package com.marcusrunge.mydefcon.firebase.interfaces

import com.marcusrunge.mydefcon.firebase.documents.DefconGroup

interface Firestore {
    suspend fun getDefconGroup() : DefconGroup
}