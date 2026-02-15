package com.marcusrunge.mydefcon.firebase.implementations

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.installations.FirebaseInstallations
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.documents.CheckItem
import com.marcusrunge.mydefcon.firebase.documents.DefconGroup
import com.marcusrunge.mydefcon.firebase.documents.Follower
import com.marcusrunge.mydefcon.firebase.interfaces.Firestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

internal class FirestoreImpl(private val base: FirebaseBase) : Firestore {
    private val tag: String = "FirestoreImpl"

    /**
     * Creates a new `DefconGroup` in Firestore.
     * This function generates a new document in the "DefconGroup" collection.
     * It attempts to associate the group with the current app installation's ID as the "Leader".
     * A timestamp is also added to mark the creation time.
     *
     * @return The unique identifier (document ID) of the newly created group.
     * @throws Exception if the document creation fails.
     */
    override suspend fun createDefconGroup(): String = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        var installationId: String? = null
        try {
            installationId = FirebaseInstallations.getInstance().id.await()
        } catch (e: Exception) {
            Log.w(tag, "Error getting installation ID", e)
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

    /**
     * Retrieves a specific `DefconGroup` and its associated sub-collections (Followers, CheckItems) from Firestore.
     * It fetches the main group document, then iterates through its "Followers" and "CheckItems" sub-collections
     * to build a complete [DefconGroup] object.
     *
     * @param documentId The unique identifier of the `DefconGroup` document.
     * @return A [DefconGroup] object populated with data from Firestore. If the document doesn't exist, an empty [DefconGroup] is returned.
     * @throws Exception if there's an error during the fetch operation.
     */
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

                val followerQuerySnapshot = try {
                    documentSnapshot.reference.collection("Followers").get().await()
                } catch (e: Exception) {
                    Log.w(tag, "Error getting follower collection for document ID: $documentId", e)
                    throw e
                }

                for (followerDocument in followerQuerySnapshot.documents) {
                    if (followerDocument.exists()) {
                        defconGroup.followers.add(
                            Follower(
                                followerDocument.id,
                                installationId = followerDocument.getString("InstallationId")
                                    .toString(),
                                isActive = followerDocument.getBoolean("IsActive") ?: false,
                                timestamp = followerDocument.getTimestamp("TimeStamp")
                                    ?.toDate()?.time ?: 0L
                            )
                        )
                    }
                }

                val checkItemQuerySnapshot = try {
                    documentSnapshot.reference.collection("CheckItems").get().await()
                } catch (e: Exception) {
                    Log.w(
                        tag,
                        "Error getting check item collection for document ID: $documentId",
                        e
                    )
                    throw e
                }

                for (checkItemDocument in checkItemQuerySnapshot.documents) {
                    if (checkItemDocument.exists()) {
                        defconGroup.checkItems.add(
                            CheckItem(
                                id = checkItemDocument.id,
                                uuid = checkItemDocument.getString("Uuid").toString(),
                                text = checkItemDocument.getString("Text").toString(),
                                defcon = checkItemDocument.getLong("Defcon")?.toInt() ?: 0,
                                created = checkItemDocument.getLong("Created") ?: 0L,
                                updated = checkItemDocument.getLong("Updated") ?: 0L
                            )
                        )
                    }
                }
                return@withContext defconGroup

            } catch (e: Exception) {
                Log.w(tag, "Error getting document with ID: $documentId", e)
                throw e
            }
        }

    /**
     * Deletes a `DefconGroup` and all its sub-collections (Followers, CheckItems) from Firestore.
     * This function performs a cascading delete, first removing all documents within the "Followers"
     * and "CheckItems" sub-collections before deleting the main `DefconGroup` document itself.
     * Batch writes are used for deleting sub-collection items for efficiency.
     *
     * @param documentId The unique identifier of the `DefconGroup` to delete.
     * @throws Exception if the deletion process fails at any stage.
     */
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
                }

                val checkItemsCollection =
                    db.collection("DefconGroup").document(documentId).collection("CheckItems")
                val checkItemQuerySnapshot = checkItemsCollection.get().await()
                if (!checkItemQuerySnapshot.isEmpty) {
                    val batch = db.batch()
                    for (checkItemDocument in checkItemQuerySnapshot.documents) {
                        batch.delete(checkItemDocument.reference)
                    }
                    batch.commit().await()
                }

                db.collection("DefconGroup").document(documentId).delete().await()
                Log.d(tag, "DefconGroup document with ID: $documentId deleted successfully.")

            } catch (e: Exception) {
                Log.w(tag, "Error deleting DefconGroup with ID: $documentId", e)
                throw e
            }
        }
    }

    /**
     * Checks if a `DefconGroup` with the specified ID exists in Firestore.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @return `true` if the group exists, `false` otherwise. Returns `false` also in case of an error.
     */
    override suspend fun checkIfDefconGroupExists(documentId: String): Boolean =
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val documentSnapshot =
                    db.collection("DefconGroup").document(documentId).get().await()
                return@withContext documentSnapshot.exists()
            } catch (e: Exception) {
                Log.w(tag, "Error checking if DefconGroup ID: $documentId exists", e)
                return@withContext false
            }
        }

    /**
     * Adds a follower to a `DefconGroup`.
     * This creates a new document in the "Followers" sub-collection of the specified `DefconGroup`.
     * The follower is marked as active by default.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower to add.
     * @throws Exception if adding the follower fails.
     */
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

    /**
     * Removes a follower from a `DefconGroup` based on their installation ID.
     * It queries the "Followers" sub-collection to find the matching document and deletes it.
     * A batch operation is used to ensure all documents matching the installation ID are removed.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower to remove.
     * @throws Exception if the removal process fails.
     */
    override suspend fun leaveDefconGroup(documentId: String, installationId: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val followersCollection = db.collection("DefconGroup").document(documentId)
                    .collection("Followers")

                val querySnapshot =
                    followersCollection.whereEqualTo("InstallationId", installationId).get().await()

                if (querySnapshot.isEmpty) {
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

    /**
     * Retrieves all followers associated with a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @return A list of [Follower] objects. Returns an empty list if the collection is empty or an error occurs.
     * @throws Exception if there is a problem fetching the followers.
     */
    override suspend fun getDefconGroupFollowers(documentId: String): List<Follower> =
        withContext(Dispatchers.IO) {
            val followers = mutableListOf<Follower>()
            val db = FirebaseFirestore.getInstance()
            try {
                val followerQuerySnapshot =
                    db.collection("DefconGroup").document(documentId).collection("Followers").get()
                        .await()

                for (followerDocument in followerQuerySnapshot.documents) {
                    if (followerDocument.exists()) {
                        followers.add(
                            Follower(
                                followerDocument.id,
                                installationId = followerDocument.getString("InstallationId")
                                    .toString(),
                                isActive = followerDocument.getBoolean("IsActive") ?: false,
                                timestamp = followerDocument.getTimestamp("TimeStamp")
                                    ?.toDate()?.time ?: 0L
                            )
                        )
                    }
                }
                return@withContext followers

            } catch (e: Exception) {
                Log.w(tag, "Error getting followers for document ID: $documentId", e)
                throw e
            }
        }

    /**
     * Checks if a specific follower is part of a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower.
     * @return `true` if the follower is in the group, `false` otherwise. Returns `false` also in case of an error.
     */
    override suspend fun checkIfFollowerInDefconGroupExists(
        documentId: String,
        installationId: String
    ): Boolean = withContext(Dispatchers.IO) {
        val db = FirebaseFirestore.getInstance()
        try {
            val documentSnapshot =
                db.collection("DefconGroup").document(documentId).get().await()
            val querySnapshot = documentSnapshot.reference.collection("Followers")
                .whereEqualTo("InstallationId", installationId).get().await()
            return@withContext !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.w(tag, "Error checking if follower in DefconGroup ID: $documentId exists", e)
            return@withContext false
        }
    }

    /**
     * Updates the status of a follower within a `DefconGroup`.
     * This function finds the follower by their installation ID and updates their `IsActive` status
     * and `TimeStamp`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower.
     * @param isActive The new status of the follower.
     * @throws Exception if the update fails.
     */
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
            } catch (e: Exception) {
                Log.w(tag, "Error updating follower status in DefconGroup ID: $documentId", e)
                throw e
            }
        }
    }

    /**
     * Adds a check item to a `DefconGroup`.
     * This function creates a new document in the "CheckItems" sub-collection of the specified `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param checkItem The [CheckItem] object to add. The `id` property is ignored.
     * @throws Exception if adding the check item fails.
     */
    override suspend fun addCheckItem(documentId: String, checkItem: CheckItem) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val checkItemData = hashMapOf(
                "Uuid" to checkItem.uuid,
                "Text" to checkItem.text,
                "Defcon" to checkItem.defcon,
                "Created" to checkItem.created,
                "Updated" to checkItem.updated
            )
            try {
                db.collection("DefconGroup").document(documentId)
                    .collection("CheckItems")
                    .add(checkItemData)
                    .await()
                Log.d(tag, "Check item added to DefconGroup ID: $documentId")
            } catch (e: Exception) {
                Log.w(tag, "Error adding check item to DefconGroup ID: $documentId", e)
                throw e
            }
        }
    }

    /**
     * Retrieves all check items associated with a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @return A list of [CheckItem] objects. Returns an empty list if the collection is empty or an error occurs.
     * @throws Exception if there is a problem fetching the check items.
     */
    override suspend fun getCheckItems(documentId: String): List<CheckItem> =
        withContext(Dispatchers.IO) {
            val checkItems = mutableListOf<CheckItem>()
            val db = FirebaseFirestore.getInstance()
            try {
                val checkItemQuerySnapshot =
                    db.collection("DefconGroup").document(documentId).collection("CheckItems").get()
                        .await()

                for (checkItemDocument in checkItemQuerySnapshot.documents) {
                    if (checkItemDocument.exists()) {
                        checkItems.add(
                            CheckItem(
                                id = checkItemDocument.id,
                                uuid = checkItemDocument.getString("Uuid").toString(),
                                text = checkItemDocument.getString("Text").toString(),
                                defcon = checkItemDocument.getLong("Defcon")?.toInt() ?: 0,
                                created = checkItemDocument.getTimestamp("Created")
                                    ?.toDate()?.time
                                    ?: 0L,
                                updated = checkItemDocument.getTimestamp("Updated")
                                    ?.toDate()?.time
                                    ?: 0L
                            )
                        )
                    }
                }
                return@withContext checkItems
            } catch (e: Exception) {
                Log.w(tag, "Error getting check item collection for document ID: $documentId", e)
                throw e
            }
        }

    /**
     * Updates a check item in a `DefconGroup` based on its UUID.
     * This function queries for a check item with a matching UUID and updates its "Text", "Defcon", and "Updated" fields.
     * A batch update is used to apply changes to all found documents, ensuring consistency if multiple documents share the same UUID.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param checkItem The [CheckItem] object containing the updated data and the UUID to match.
     * @throws Exception if the update process fails.
     */
    override suspend fun updateCheckItem(
        documentId: String,
        checkItem: CheckItem
    ) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val checkItemsCollection = db.collection("DefconGroup").document(documentId)
                    .collection("CheckItems")
                val querySnapshot =
                    checkItemsCollection.whereEqualTo("Uuid", checkItem.uuid).get().await()

                if (querySnapshot.isEmpty) {
                    Log.w(
                        tag,
                        "No check item found with UUID ${checkItem.uuid} in DefconGroup ID: $documentId to update."
                    )
                    return@withContext
                }

                val updates = mapOf(
                    "Text" to checkItem.text,
                    "Defcon" to checkItem.defcon,
                    "Updated" to checkItem.updated
                )

                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.update(document.reference, updates)
                }
                batch.commit().await()
                Log.d(tag, "CheckItem with uuid ${checkItem.uuid} updated.")
            } catch (e: Exception) {
                Log.w(tag, "Error updating check item with UUID: ${checkItem.uuid}", e)
                throw e
            }
        }
    }

    /**
     * Deletes a check item from a `DefconGroup` using its UUID.
     * It queries the "CheckItems" sub-collection to find any documents matching the provided UUID and deletes them.
     * A batch operation ensures that all matching items are removed.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param checkItemUuid The unique identifier (UUID) of the check item to delete.
     * @throws Exception if the deletion fails.
     */
    override suspend fun deleteCheckItem(documentId: String, checkItemUuid: String) {
        withContext(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            try {
                val checkItemsCollection = db.collection("DefconGroup").document(documentId)
                    .collection("CheckItems")
                val querySnapshot =
                    checkItemsCollection.whereEqualTo("Uuid", checkItemUuid).get().await()

                if (querySnapshot.isEmpty) {
                    return@withContext
                }

                val batch = db.batch()
                for (document in querySnapshot.documents) {
                    batch.delete(document.reference)
                }
                batch.commit().await()
                Log.d(tag, "Check item with UUID: $checkItemUuid deleted successfully.")
            } catch (e: Exception) {
                Log.w(tag, "Error deleting check item with UUID: $checkItemUuid", e)
                throw e
            }
        }
    }

    internal companion object {
        private var instance: Firestore? = null

        /**
         * Creates and provides a singleton instance of the [Firestore] interface.
         *
         * @param base The [FirebaseBase] dependency required for initialization.
         * @return A singleton instance of [Firestore].
         */
        fun create(base: FirebaseBase): Firestore = when {
            instance != null -> instance!!
            else -> {
                instance = FirestoreImpl(base)
                instance!!
            }
        }
    }
}
