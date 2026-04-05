package com.marcusrunge.mydefcon.implementations

import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase
import com.marcusrunge.mydefcon.interfaces.CheckListSynchronization
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * An implementation of the [CheckListSynchronization] interface.
 *
 * This class is responsible for synchronizing the checklist data between the Firebase
 * remote data source and the local database. It fetches the latest checklist from
 * Firebase, updates the local database, and then notifies observers of the change.
 *
 * @param core The core component of the application, used for accessing shared services like the LiveDataManager.
 * @param data The data layer component, used for accessing the local database.
 * @param firebase The Firebase integration component, used for accessing remote data.
 */
class CheckListSynchronizationImpl(
    private val core: Core,
    private val data: Data,
    private val firebase: Firebase
) : CheckListSynchronization {
    /**
     * Synchronizes the checklist.
     *
     * This function fetches the complete checklist from Firebase, clears the local
     * checklist table, and inserts the new items. After the local database is updated,
     * it emits a notification through the [com.marcusrunge.mydefcon.core.interfaces.LiveDataManager] to inform observers that the
     * checklist data has changed. This operation is performed on the IO dispatcher to
     * avoid blocking the main thread.
     */
    override suspend fun syncCheckList() {
        withContext(Dispatchers.IO) {
            val joinedDefconGroupId = core.preferences?.joinedDefconGroupId
            val createdDefconGroupId = core.preferences?.createdDefconGroupId

            if (joinedDefconGroupId?.isNotBlank() == true) {
                // TODO: Implement pull sync
                core.liveDataManager?.emitCheckListChange(CheckListSynchronizationImpl::class.java)
            } else if (createdDefconGroupId?.isNotBlank() == true) {
                val firebaseCheckItems = firebase.firestore.getCheckItems(createdDefconGroupId)
                val repositoryCheckItems = data.repository.checkItems.getAll()

                repositoryCheckItems.forEach { repositoryCheckItem ->
                    val firebaseCheckItem =
                        firebaseCheckItems.find { it.uuid == repositoryCheckItem.uuid }

                    val checkItem = com.marcusrunge.mydefcon.firebase.documents.CheckItem(
                        id = firebaseCheckItem?.id ?: "",
                        uuid = repositoryCheckItem.uuid,
                        text = repositoryCheckItem.text ?: "",
                        defcon = repositoryCheckItem.defcon,
                        created = repositoryCheckItem.created,
                        updated = repositoryCheckItem.updated
                    )

                    if (firebaseCheckItem == null) {
                        firebase.firestore.addCheckItem(createdDefconGroupId, checkItem)
                    } else {
                        firebase.firestore.updateCheckItem(createdDefconGroupId, checkItem)
                    }
                }

                firebaseCheckItems.forEach { firebaseCheckItem ->
                    if (repositoryCheckItems.none { it.uuid == firebaseCheckItem.uuid }) {
                        firebase.firestore.deleteCheckItem(createdDefconGroupId, firebaseCheckItem.uuid)
                    }
                }
            }
        }
    }

    internal companion object {
        @Volatile
        private var instance: CheckListSynchronization? = null

        /**
         * Creates and returns a singleton instance of [CheckListSynchronization].
         *
         * This function ensures that only one instance of [CheckListSynchronizationImpl] is created
         * and used throughout the application. It uses a thread-safe, double-checked locking
         * mechanism for robust singleton implementation.
         *
         * @param core The core component of the application.
         * @param data The data layer component.
         * @param firebase The Firebase integration component.
         * @return The singleton instance of [CheckListSynchronization].
         */
        fun create(core: Core, data: Data, firebase: Firebase): CheckListSynchronization =
            instance ?: synchronized(this) {
                instance ?: CheckListSynchronizationImpl(core, data, firebase).also { instance = it }
            }
    }
}
