package com.marcusrunge.mydefcon.data

import android.content.Context
import com.marcusrunge.mydefcon.data.implementations.DataImpl
import com.marcusrunge.mydefcon.data.interfaces.Data

/**
 * A factory for creating [Data] instances.
 */
interface DataFactory {
    /**
     * Creates a data instance.
     *
     * @param context The application context.
     * @return The [Data] instance.
     * @see Data
     */
    fun create(context: Context?): Data
}

/**
 * An implementation of [DataFactory] that provides a singleton [Data] instance.
 *
 * This object ensures that only one instance of the data layer is created and used
 * throughout the application. It uses a thread-safe, double-checked locking pattern
 * to initialize the [Data] instance.
 */
object DataFactoryImpl : DataFactory {
    @Volatile
    private var data: Data? = null

    /**
     * Creates or retrieves the singleton instance of [Data].
     *
     * If the [data] instance is null, it initializes a new [DataImpl] instance in a
     * thread-safe way. On subsequent calls, it returns the existing instance.
     *
     * @param context The application context, required for the first-time initialization.
     * @return The singleton instance of [Data].
     */
    override fun create(context: Context?): Data {
        return data ?: synchronized(this) {
            data ?: DataImpl(context).also { data = it }
        }
    }
}
