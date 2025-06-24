package com.marcusrunge.mydefcon.firebase.implementations

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.documents.DefconGroup
import com.marcusrunge.mydefcon.firebase.documents.Follower
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


internal class FirestoreImpl(private val base: FirebaseBase) : Firestore {
    val TAG: String = "FirestoreImpl"
    override suspend fun getDefconGroup(): DefconGroup = withContext(Dispatchers.IO) {
        val defconGroup = DefconGroup()
        val db = FirebaseFirestore.getInstance()
        val querySnapshot = try {
            db.collection("DefconGroup").get().await()
        } catch (e: Exception) {
            Log.w(TAG, "Error getting document: ", e)
            throw e
        }
        if (querySnapshot.isEmpty) {
            Log.d(TAG, "Collection is empty")
            return@withContext defconGroup
        }
        val document = querySnapshot.documents.first()
        if (!document.exists()) {
            Log.d(TAG, "No such document")
            return@withContext defconGroup // Return empty group
        }
        defconGroup.id = document.id
        defconGroup.leader = document.getString("Leader").toString()
        defconGroup.timestamp = document.getTimestamp("TimeStamp")!!.toDate().time
        val followerQuerySnapshot = try {
            document.reference.collection("Followers").get().await()
        } catch (e: Exception) {
            Log.w(TAG, "Error getting follower collection: ", e)
            throw e
        }
        if (followerQuerySnapshot.isEmpty) {
            Log.d(TAG, "Follower collection is empty")
            return@withContext defconGroup // Return group with no followers
        }
        for (followerDocument in followerQuerySnapshot.documents) {
            if (followerDocument.exists()) {
                defconGroup.followers.add(
                    Follower(
                        followerDocument.id,
                        followerDocument.getString("Token").toString(),
                        followerDocument.getTimestamp("TimeStamp")!!.toDate().time
                    )
                )
            } else {
                Log.d(TAG, "No such follower document")
            }
        }
        return@withContext defconGroup
    }

    override suspend fun createDefconGroup(): String {
        TODO("Not yet implemented")
    }

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