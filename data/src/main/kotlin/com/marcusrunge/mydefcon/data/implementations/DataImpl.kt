package com.marcusrunge.mydefcon.data.implementations

import android.content.Context
import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.interfaces.Data

/**
 * An internal implementation of the [Data] interface.
 *
 * This class serves as the concrete implementation for the data layer's entry point.
 * It extends [DataBase] to leverage underlying database functionalities and is responsible
 * for initializing the repository.
 *
 * This class is not intended for direct use but should be instantiated via its corresponding factory.
 *
 * @param context The application context, used for initializing the database via the [DataBase] parent class.
 * @see com.marcusrunge.mydefcon.data.DataFactory
 */
internal class DataImpl(context: Context?) : DataBase(context), Data {
    init {
        // Initializes the repository implementation inherited from DataBase.
        _repository = RepositoryImpl.create(this)
    }
}
