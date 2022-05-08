package com.marcusrunge.mydefcon.data.interfaces

import androidx.room.*
import com.marcusrunge.mydefcon.data.entities.CheckItem

@Dao
interface CheckItems {

    /**
     * Gets all check items.
     * @see CheckItem
     * @return A list of check items.
     */
    @Query("SELECT * FROM checkitem")
    fun getAll(): List<CheckItem>

    /**
     * Updates a check item.
     * @see CheckItem
     * @param checkItem The check item.
     */
    @Update
    fun update(checkItem: CheckItem)

    /**
     * Inserts a check item.
     * @see CheckItem
     * @param checkItem The check item.
     * @return The check item's id.
     */
    @Insert
    fun insert(checkItem: CheckItem): Long

    /**
     * Deletes a check item.
     * @see CheckItem
     * @param checkItem The check item.
     */
    @Delete
    fun delete(checkItem: CheckItem)

    /**
     * Deletes a check item by it's id.
     * @see CheckItem
     * @param id The item's id.
     */
    @Query("DELETE FROM checkitem WHERE id = :id")
    fun delete(id: Int)

    /**
     * Deletes all check items.
     */
    @Query("DELETE FROM checkitem")
    fun deleteAll()
}