package com.marcusrunge.mydefcon.data.bases

import androidx.room.Database
import androidx.room.RoomDatabase
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.CheckItems

@Database(entities = [CheckItem::class], version = 1, exportSchema = false)
internal abstract class MyDefconDatabase : RoomDatabase() {
    abstract fun checkItems(): CheckItems
}