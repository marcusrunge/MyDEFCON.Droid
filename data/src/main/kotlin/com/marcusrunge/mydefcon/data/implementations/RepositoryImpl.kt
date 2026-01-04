package com.marcusrunge.mydefcon.data.implementations

import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.bases.RepositoryBase
import com.marcusrunge.mydefcon.data.interfaces.CheckItems
import com.marcusrunge.mydefcon.data.interfaces.Repository

/**
 * An internal implementation of the [Repository] interface.
 *
 * This class serves as the concrete implementation of the main repository. It extends
 * [RepositoryBase] to gain access to the underlying database and is responsible for
 * providing instances of the Data Access Objects (DAOs).
 *
 * It follows a singleton pattern, ensuring that only one instance of the repository
 * exists. This class is not intended for direct instantiation but should be created
 * via the `create` factory method in the companion object.
 *
 * @param dataBase The [DataBase] instance which provides access to the database context.
 */
internal class RepositoryImpl private constructor(dataBase: DataBase) : RepositoryBase(dataBase.context), Repository {

    init {
        // The `myDefconDatabase` property is inherited from RepositoryBase, providing access
        // to the Room database instance. The `checkItems()` method returns the DAO.
        // The `_checkItems` backing field is defined in the RepositoryBase class.
        _checkItems = myDefconDatabase?.checkItems()!!
    }

    /**
     * Provides access to the Data Access Object for check items.
     *
     * This property is implemented by returning the backing field `_checkItems`,
     * which is initialized from the database in the `init` block.
     *
     * @see CheckItems
     */
    override val checkItems: CheckItems
        get() = _checkItems

    /**
     * A companion object for creating a singleton instance of [RepositoryImpl].
     */
    companion object {
        @Volatile
        private var instance: Repository? = null

        /**
         * Creates or retrieves the singleton instance of the [Repository].
         *
         * This method uses a thread-safe, double-checked locking pattern to ensure that
         * only one instance of the repository is created.
         *
         * @param dataBase The [DataBase] instance, required for the first-time initialization.
         * @return The singleton [Repository] instance.
         */
        fun create(dataBase: DataBase): Repository {
            return instance ?: synchronized(this) {
                instance ?: RepositoryImpl(dataBase).also { instance = it }
            }
        }
    }
}
