package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import androidx.room.Room
import com.marcusrunge.mydefcon.data.interfaces.CheckItems
import com.marcusrunge.mydefcon.data.interfaces.Repository

internal abstract class RepositoryBase(context: Context?) : Repository {
    protected lateinit var _checkItems: CheckItems

    internal val myDefconDatabase = context?.let {
        Room.databaseBuilder(it, MyDefconDatabase::class.java, "mydefcon_database").build()
    }
}