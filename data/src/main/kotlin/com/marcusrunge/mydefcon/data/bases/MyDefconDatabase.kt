package com.marcusrunge.mydefcon.data.bases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.CheckItems

/**
 * The Room database for the MyDEFCON application.
 *
 * This abstract class defines the database configuration and serves as the main access point
 * to the persisted data. It lists the entities contained within the database and the DAOs
 * for interacting with them.
 *
 * @see RoomDatabase
 */
@Database(entities = [CheckItem::class], version = 1, exportSchema = false)
internal abstract class MyDefconDatabase : RoomDatabase() {
    /**
     * Provides the Data Access Object (DAO) for check items.
     *
     * @return The [CheckItems] instance for interacting with the `checkitem` table.
     * @see CheckItems
     */
    abstract fun checkItems(): CheckItems
}
