package com.marcusrunge.mydefcon.firebase.implementations

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.documents.DefconGroup
import com.marcusrunge.mydefcon.firebase.documents.Follower
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


internal class FirestoreImpl(private val base: FirebaseBase) : Firestore {
    val tag: String = "FirestoreImpl"
    override suspend fun getDefconGroup(documentId: String): DefconGroup =
        withContext(Dispatchers.IO) {
            val defconGroup = DefconGroup()
            val db = FirebaseFirestore.getInstance()
            try {
                // Get the specific document by its ID
                val documentSnapshot =
                    db.collection("DefconGroup").document(documentId).get().await()

                if (!documentSnapshot.exists()) {
                    Log.d(tag, "No such document with ID: $documentId")
                    return@withContext defconGroup // Return empty group if document not found
                }

                // Populate DefconGroup from the document
                defconGroup.id = documentSnapshot.id
                defconGroup.leader = documentSnapshot.getString("Leader").toString()
                // Ensure TimeStamp is not null before converting
                documentSnapshot.getTimestamp("TimeStamp")?.toDate()?.time?.let {
                    defconGroup.timestamp = it
                } ?: run {
                    Log.w(tag, "TimeStamp is null for document ID: $documentId")
                    // Handle the case where TimeStamp is null, perhaps by setting a default or logging
                }


                // Fetch followers
                val followerQuerySnapshot = try {
                    documentSnapshot.reference.collection("Followers").get().await()
                } catch (e: Exception) {
                    Log.w(tag, "Error getting follower collection for document ID: $documentId", e)
                    throw e // Or handle appropriately
                }

                if (followerQuerySnapshot.isEmpty) {
                    Log.d(tag, "Follower collection is empty for document ID: $documentId")
                    // No followers, but the group itself exists
                } else {
                    for (followerDocument in followerQuerySnapshot.documents) {
                        if (followerDocument.exists()) {
                            defconGroup.followers.add(
                                Follower(
                                    followerDocument.id,
                                    followerDocument.getString("Token").toString(),
                                    // Ensure TimeStamp is not null for follower
                                    followerDocument.getTimestamp("TimeStamp")?.toDate()?.time
                                        ?: 0L // Default to 0 or handle
                                )
                            )
                        } else {
                            Log.d(tag, "No such follower document in group ID: $documentId")
                        }
                    }
                }
                return@withContext defconGroup

            } catch (e: Exception) {
                Log.w(tag, "Error getting document with ID: $documentId", e)
                throw e // Or handle the error as appropriate for your application
            }
        }


    override suspend fun createDefconGroup(): String = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        var installationId: String? = null
        try {
            installationId = FirebaseInstallations.getInstance().id.await()
        } catch (e: Exception) {
        }

        val defconGroupData = hashMapOf(
            "Leader" to installationId, // Set Leader to the fetched FCM token
            "TimeStamp" to Timestamp.now()
        )

        try {
            val documentReference = db.collection("DefconGroup")
                .add(defconGroupData)
                .await()
            Log.d(tag, "DefconGroup created with ID: ${documentReference.id} and Leader: fcmToken")
            return@withContext documentReference.id
        } catch (e: Exception) {
            Log.w(tag, "Error creating DefconGroup document", e)
            throw e
        }
    }

    override suspend fun deleteDefconGroup(documentId: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                // First, delete all documents in the "Followers" subcollection
                val followersCollection =
                    db.collection("DefconGroup").document(documentId).collection("Followers")
                val followerQuerySnapshot = followersCollection.get().await()
                if (!followerQuerySnapshot.isEmpty) {
                    val batch = db.batch()
                    for (followerDocument in followerQuerySnapshot.documents) {
                        batch.delete(followerDocument.reference)
                    }
                    batch.commit().await()
                    Log.d(tag, "All followers in DefconGroup ID: $documentId deleted.")
                } else {
                    Log.d(tag, "No followers to delete in DefconGroup ID: $documentId.")
                }

                // After deleting followers, delete the DefconGroup document itself
                db.collection("DefconGroup").document(documentId).delete().await()
                Log.d(tag, "DefconGroup document with ID: $documentId deleted successfully.")

            } catch (e: Exception) {
                Log.w(tag, "Error deleting DefconGroup with ID: $documentId", e)
                // Propagate the exception or handle it as appropriate for your application
                // For example, you might throw a custom exception or return a status code
                throw e
            }
        }
    }

    override suspend fun joinDefconGroup(documentId: String, fcmToken: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val followerData = hashMapOf(
                "Token" to fcmToken,
                "TimeStamp" to Timestamp.now()
            )
            try {
                db.collection("DefconGroup").document(documentId)
                    .collection("Followers")
                    .add(followerData) // Firestore will auto-generate an ID for the follower
                    .await()
                Log.d(tag, "Follower with token $fcmToken added to DefconGroup ID: $documentId")
            } catch (e: Exception) {
                Log.w(tag, "Error adding follower to DefconGroup ID: $documentId", e)
                throw e
            }
        }
    }

    override suspend fun leaveDefconGroup(documentId: String, fcmToken: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val followersCollection = db.collection("DefconGroup").document(documentId)
                    .collection("Followers")

                // Query for the follower document with the matching FCM token
                val querySnapshot =
                    followersCollection.whereEqualTo("Token", fcmToken).get().await()

                if (querySnapshot.isEmpty) {
                    Log.d(
                        tag,
                        "No follower found with token $fcmToken in DefconGroup ID: $documentId to remove."
                    )
                    return@withContext // Or throw an exception if this case is an error
                }

                // Delete all documents that match the query (should ideally be one)
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit().await()
                Log.d(tag, "Follower with token $fcmToken removed from DefconGroup ID: $documentId")

            } catch (e: Exception) {
                Log.w(tag, "Error removing follower from DefconGroup ID: $documentId", e)
                throw e
            }
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