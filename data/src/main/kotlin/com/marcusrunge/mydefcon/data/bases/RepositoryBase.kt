package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import androidx.room.Room
import com.marcusrunge.mydefcon.data.interfaces.CheckItems

/**
 * An abstract base class for repositories.
 *
 * This class provides common functionality for repositories, such as initializing the
 * database and holding the database instance. Subclasses are responsible for initializing
 * the DAOs.
 *
 * @param context The application context, which is used to build the Room database instance.
 * @throws IllegalStateException if the provided context is null.
 */
internal abstract class RepositoryBase(context: Context?) {
    /**
     * The backing field for the [CheckItems].
     *
     * This property is intended to be initialized by a subclass (e.g., `RepositoryImpl`) and holds
     * the concrete implementation of the [CheckItems]. It is `protected` to be accessible
     * only within this class and its subclasses.
     */
    protected lateinit var _checkItems: CheckItems

    /**
     * The Room database instance for the application.
     *
     * This property provides access to the `MyDefconDatabase` instance, which is built
     * using the application context. It is marked as `internal` to be accessible within
     * the same module.
     */
    internal val myDefconDatabase: MyDefconDatabase

    init {
        // A non-null context is required to initialize the database.
        requireNotNull(context) { "Context must not be null for database initialization." }
        myDefconDatabase = Room.databaseBuilder(
                context,
                MyDefconDatabase::class.java, "mydefcon_database"
            ).fallbackToDestructiveMigration(false).build()
    }
}
