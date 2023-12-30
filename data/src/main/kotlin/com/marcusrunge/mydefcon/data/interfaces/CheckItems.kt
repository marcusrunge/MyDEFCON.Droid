package com.marcusrunge.mydefcon.data.interfaces

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.marcusrunge.mydefcon.data.entities.CheckItem

@Dao
interface CheckItems {

    /**
     * Gets all check items as mutable live data.
     * @see CheckItem
     * @return A live data mutable list of check items.
     */
    @Query("SELECT * FROM checkitem WHERE is_deleted=0")
    fun getAllMutableLive(): LiveData<MutableList<CheckItem>>

    /**
     * Gets all check items.
     * @see CheckItem
     * @return A list of check items.
     */
    @Query("SELECT * FROM checkitem WHERE is_deleted=0")
    fun getAll(): List<CheckItem>

    /**
     * Gets all check items.
     * @see CheckItem
     * @param defcon The Defcon status
     * @return A list of check items.
     */
    @Query("SELECT * FROM checkitem WHERE defcon =:defcon AND is_deleted=0")
    fun getAllMutableLive(defcon: Int): LiveData<MutableList<CheckItem>>

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