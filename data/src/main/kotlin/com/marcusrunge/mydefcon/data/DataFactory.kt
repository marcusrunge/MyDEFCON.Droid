package com.marcusrunge.mydefcon.data

import android.content.Context
import com.marcusrunge.mydefcon.data.implementations.DataImpl
import com.marcusrunge.mydefcon.data.interfaces.Data

interface DataFactory {
    /**
     * Creates the data instance
     * @see Data
     * @param context The application context
     */
    fun create(context: Context?): Data
}

class DataFactoryImpl {
    companion object : DataFactory {
        private var data: Data? = null
        override fun create(context: Context?): Data = when {
            data != null -> data!!
            else -> {
                data = DataImpl(context)
                data!!
            }
        }
    }
}