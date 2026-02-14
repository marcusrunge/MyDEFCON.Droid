package com.marcusrunge.mydefcon.firebase.interfaces

import com.marcusrunge.mydefcon.firebase.documents.CheckItem
import com.marcusrunge.mydefcon.firebase.documents.DefconGroup
import com.marcusrunge.mydefcon.firebase.documents.Follower

/**
 * Defines the contract for interacting with the Firestore database.
 *
 * This interface provides a set of suspend functions for performing CRUD (Create, Read, Update, Delete)
 * operations on `DefconGroup` and `Follower` documents in Firestore. It abstracts the underlying
 * Firestore API calls, offering a clean and domain-specific way to manage data.
 */
interface Firestore {
    /**
     * Retrieves a specific `DefconGroup` from Firestore.
     *
     * @param documentId The unique identifier of the `DefconGroup` document.
     * @return The [DefconGroup] object.
     * @see DefconGroup
     */
    suspend fun getDefconGroup(documentId: String): DefconGroup

    /**
     * Creates a new `DefconGroup` in Firestore.
     *
     * @return The unique identifier (document ID) of the newly created group.
     */
    suspend fun createDefconGroup(): String

    /**
     * Deletes a `DefconGroup` from Firestore.
     *
     * @param documentId The unique identifier of the `DefconGroup` to delete.
     */
    suspend fun deleteDefconGroup(documentId: String)

    /**
     * Adds a follower to a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower to add.
     */
    suspend fun joinDefconGroup(documentId: String, installationId: String)

    /**
     * Removes a follower from a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower to remove.
     */
    suspend fun leaveDefconGroup(documentId: String, installationId: String)

    /**
     * Checks if a `DefconGroup` with the specified ID exists in Firestore.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @return `true` if the group exists, `false` otherwise.
     */
    suspend fun checkIfDefconGroupExists(documentId: String): Boolean

    /**
     * Checks if a specific follower is part of a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower.
     * @return `true` if the follower is in the group, `false` otherwise.
     */
    suspend fun checkIfFollowerInDefconGroupExists(
        documentId: String,
        installationId: String
    ): Boolean

    /**
     * Retrieves all followers associated with a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @return A list of [Follower] objects.
     * @see Follower
     */
    suspend fun getDefconGroupFollowers(documentId: String): List<Follower>

    /**
     * Updates the status of a follower within a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @param installationId The unique installation ID of the follower.
     * @param isActive The new status of the follower.
     */
    suspend fun updateFollowerStatus(documentId: String, installationId: String, isActive: Boolean)

    /**
     * Adds a check item to a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     */
    suspend fun addCheckItem(documentId: String, checkItem: CheckItem)

    /**
     * Updates a check item in a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     */
    suspend fun updateCheckItem(documentId: String, checkItem: CheckItem)
    /**
     * Retrieves all check items associated with a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     * @return A list of [CheckItem] objects.
     *
     * @see CheckItem
     */
    suspend fun getCheckItems(documentId: String): List<CheckItem>

    /**
     * Deletes a check item from a `DefconGroup`.
     *
     * @param documentId The unique identifier of the `DefconGroup`.
     */
    suspend fun deleteCheckItem(documentId: String, checkItemId: String)
}
