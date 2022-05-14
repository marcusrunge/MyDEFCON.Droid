package com.marcusrunge.mydefcon.data.bases

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.marcusrunge.mydefcon.data.entities.CheckItem
import com.marcusrunge.mydefcon.data.interfaces.CheckItems
import com.marcusrunge.mydefcon.data.utils.Converters

@Database(entities = [CheckItem::class], version = 1)
@TypeConverters(Converters::class)
internal abstract class MyDefconDatabase : RoomDatabase() {
    abstract fun checkItems(): CheckItems
}