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
                val documentSnapshot =
                    db.collection("DefconGroup").document(documentId).get().await()

                if (!documentSnapshot.exists()) {
                    Log.d(tag, "No such document with ID: $documentId")
                    return@withContext defconGroup
                }

                defconGroup.id = documentSnapshot.id
                defconGroup.leader = documentSnapshot.getString("Leader").toString()
                documentSnapshot.getTimestamp("TimeStamp")?.toDate()?.time?.let {
                    defconGroup.timestamp = it
                } ?: run {
                    Log.w(tag, "TimeStamp is null for document ID: $documentId")
                }


                // Fetch followers
                val followerQuerySnapshot = try {
                    documentSnapshot.reference.collection("Followers").get().await()
                } catch (e: Exception) {
                    Log.w(tag, "Error getting follower collection for document ID: $documentId", e)
                    throw e
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
                                    installationId = followerDocument.getString("InstallationId").toString(),
                                    isActive = followerDocument.getBoolean("IsActive")?: false,
                                    timestamp = followerDocument.getTimestamp("TimeStamp")?.toDate()?.time ?: 0L
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
                throw e
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
            "Leader" to installationId,
            "TimeStamp" to Timestamp.now()
        )

        try {
            val documentReference = db.collection("DefconGroup")
                .add(defconGroupData)
                .await()
            Log.d(
                tag,
                "DefconGroup created with ID: ${documentReference.id} and Leader: $installationId"
            )
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

                db.collection("DefconGroup").document(documentId).delete().await()
                Log.d(tag, "DefconGroup document with ID: $documentId deleted successfully.")

            } catch (e: Exception) {
                Log.w(tag, "Error deleting DefconGroup with ID: $documentId", e)
                throw e
            }
        }
    }

    override suspend fun joinDefconGroup(documentId: String, installationId: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val followerData = hashMapOf(
                "InstallationId" to installationId,
                "IsActive" to true,
                "TimeStamp" to Timestamp.now()
            )
            try {
                db.collection("DefconGroup").document(documentId)
                    .collection("Followers")
                    .add(followerData)
                    .await()
                Log.d(
                    tag,
                    "Follower with installation ID $installationId added to DefconGroup ID: $documentId"
                )
            } catch (e: Exception) {
                Log.w(tag, "Error adding follower to DefconGroup ID: $documentId", e)
                throw e
            }
        }
    }

    override suspend fun leaveDefconGroup(documentId: String, installationId: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val followersCollection = db.collection("DefconGroup").document(documentId)
                    .collection("Followers")

                val querySnapshot =
                    followersCollection.whereEqualTo("InstallationId", installationId).get().await()

                if (querySnapshot.isEmpty) {
                    Log.d(
                        tag,
                        "No follower found with installation ID $installationId in DefconGroup ID: $documentId to remove."
                    )
                    return@withContext
                }

                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit().await()
                Log.d(
                    tag,
                    "Follower with installation ID $installationId removed from DefconGroup ID: $documentId"
                )

            } catch (e: Exception) {
                Log.w(tag, "Error removing follower from DefconGroup ID: $documentId", e)
                throw e
            }
        }
    }

    override suspend fun checkIfDefconGroupExists(documentId: String): Boolean {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val documentSnapshot =
                    db.collection("DefconGroup").document(documentId).get().await()
                return@withContext documentSnapshot.exists()
            } catch (e: Exception) {
                Log.w(tag, "Error checking if DefconGroup ID: $documentId exists", e)
                throw e
            }
        }
        return false
    }

    override suspend fun checkIfFollowerInDefconGroupExists(
        documentId: String,
        installationId: String
    ): Boolean {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val documentSnapshot =
                    db.collection("DefconGroup").document(documentId).get().await()
                val querySnapshot = documentSnapshot.reference.collection("Followers")
                    .whereEqualTo("InstallationId", installationId).get().await()
                return@withContext !querySnapshot.isEmpty
            } catch (e: Exception) {
                Log.w(tag, "Error checking if DefconGroup ID: $documentId exists", e)
                throw e
            }
        }
        return false
    }

    override suspend fun getDefconGroupFollowers(documentId: String): List<Follower> =
        withContext(Dispatchers.IO) {
            val followers = mutableListOf<Follower>()
            val db = FirebaseFirestore.getInstance()
            try {
                val followerQuerySnapshot =
                    db.collection("DefconGroup").document(documentId).collection("Followers").get()
                        .await()

                if (followerQuerySnapshot.isEmpty) {
                    Log.d(tag, "Follower collection is empty for document ID: $documentId")
                } else {
                    for (followerDocument in followerQuerySnapshot.documents) {
                        if (followerDocument.exists()) {
                            followers.add(
                                Follower(
                                    followerDocument.id,
                                    installationId = followerDocument.getString("InstallationId").toString(),
                                    isActive = followerDocument.getBoolean("IsActive") ?: false,
                                    timestamp = followerDocument.getTimestamp("TimeStamp")?.toDate()?.time ?: 0L
                                )
                            )
                        } else {
                            Log.d(tag, "No such follower document in group ID: $documentId")
                        }
                    }
                }
                return@withContext followers

            } catch (e: Exception) {
                Log.w(tag, "Error getting followers for document ID: $documentId", e)
                throw e
            }
        }

    override suspend fun updateFollowerStatus(
        documentId: String,
        installationId: String,
        isActive: Boolean
    ) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val followersCollection = db.collection("DefconGroup").document(documentId)
                    .collection("Followers")

                val querySnapshot =
                    followersCollection.whereEqualTo("InstallationId", installationId).get().await()

                if (querySnapshot.isEmpty) {
                    Log.d(
                        tag,
                        "No follower found with installation ID $installationId in DefconGroup ID: $documentId to update."
                    )
                    return@withContext
                }
                val data = mapOf(
                    "IsActive" to isActive,
                    "TimeStamp" to Timestamp.now()
                )
                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.update(document.reference, data)
                }
                batch.commit().await()
                Log.d(
                    tag,
                    "Follower status updated for installation ID $installationId in DefconGroup ID: $documentId"
                )

            } catch (e: Exception) {
                Log.w(tag, "Error updating follower status in DefconGroup ID: $documentId", e)
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
