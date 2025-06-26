package com.marcusrunge.mydefcon.firebase.implementations

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
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

    override suspend fun createDefconGroup(): String = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        var fcmToken: String? = null
        try {
            fcmToken = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.w(TAG, "Fetching FCM registration token failed", e)
            // Handle the error appropriately, e.g., throw, return an error state, or proceed without a token
            // For this example, we'll throw to indicate failure to get the token
            throw IllegalStateException("Failed to fetch FCM token", e)
        }

        // Ensure the token was retrieved
        if (fcmToken == null) {
            Log.e(TAG, "FCM token is null, cannot create DefconGroup with leader token.")
            throw IllegalStateException("FCM token retrieval resulted in null.")
        }

        val defconGroupData = hashMapOf(
            "Leader" to fcmToken, // Set Leader to the fetched FCM token
            "TimeStamp" to com.google.firebase.Timestamp.now()
            // Add other fields of DefconGroup here if necessary
        )

        try {
            val documentReference = db.collection("DefconGroup")
                .add(defconGroupData)
                .await()
            Log.d(TAG, "DefconGroup created with ID: ${documentReference.id} and Leader: $fcmToken")
            return@withContext documentReference.id
        } catch (e: Exception) {
            Log.w(TAG, "Error creating DefconGroup document", e)
            throw e // Or handle the error as appropriate for your application
        }
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