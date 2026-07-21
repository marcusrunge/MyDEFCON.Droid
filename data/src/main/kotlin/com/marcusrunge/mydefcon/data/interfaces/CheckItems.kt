package com.marcusrunge.mydefcon.data.interfaces

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import com.marcusrunge.mydefcon.data.entities.CheckItem
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [CheckItem] entities.
 *
 * This interface provides methods for interacting with the `checkitem` table in the database.
 * It includes operations for querying, inserting, updating, and deleting check items.
 *
*/
@Dao
interface CheckItems {

    /**
     * Retrieves all check items as a [Flow] list.
     *
     * This method observes the `checkitem` table for changes and automatically updates the UI
     * when the data changes. It only includes items that have not been marked as deleted.
     *
     * @return A [Flow] list of all non-deleted [CheckItem] instances.
     * @see CheckItem
     */
    @Query("SELECT * FROM checkitem WHERE is_deleted=0")
    fun getAllAsFlowList(): Flow<List<CheckItem>>

    /**
     * Retrieves all check items for a specific DEFCON level as a [Flow] list.
     *
     * This method observes the `checkitem` table and provides real-time updates for items
     * matching the given DEFCON level. It only includes items that have not been marked as deleted.
     *
     * @param defcon The DEFCON level to filter by.
     * @return A [Flow] list of non-deleted [CheckItem] instances for the specified DEFCON level.
     * @see CheckItem
     */
    @Query("SELECT * FROM checkitem WHERE defcon =:defcon AND is_deleted=0")
    fun getAllAsFlowList(defcon: Int): Flow<List<CheckItem>>

    /**
     * Retrieves all non-deleted check items from the database.
     *
     * @return A list of all [CheckItem] instances that are not marked as deleted.
     * @see CheckItem
     */
    @Query("SELECT * FROM checkitem WHERE is_deleted=0")
    suspend fun getAllAsList(): List<CheckItem>

    /**
     * Inserts a new check item into the database.
     *
     * @param checkItem The [CheckItem] to be inserted.
     * @return The ID of the newly inserted item.
     * @see CheckItem
     */
    @Insert
    suspend fun insert(checkItem: CheckItem): Long

    /**
     * Updates an existing check item in the database.
     *
     * @param checkItem The [CheckItem] to be updated.
     * @see CheckItem
     */
    @Update
    suspend fun update(checkItem: CheckItem)

    /**
     * Deletes a specific check item from the database.
     *
     * @param checkItem The [CheckItem] to be deleted.
     * @see CheckItem
     */
    @Delete
    suspend fun delete(checkItem: CheckItem)

    /**
     * Deletes a check item from the database by its unique ID.
     *
     * @param id The ID of the [CheckItem] to be deleted.
     * @see CheckItem
     */
    @Query("DELETE FROM checkitem WHERE id = :id")
    suspend fun delete(id: Int)

    /**
     * Deletes all check items from the database.
     *
     * This operation is irreversible and will remove all entries from the `checkitem` table.
     */
    @Query("DELETE FROM checkitem")
    suspend fun deleteAll()
}
