package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.data.interfaces.Repository

/**
 * An abstract base class for the data layer.
 *
 * This class provides the basic structure for the data layer's entry point by implementing
 * the [Data] interface. It holds the application [Context] and manages the [Repository]
 * instance, which must be initialized by subclasses.
 *
 * @param context The application context, which can be null. It is marked as internal to be
 * accessible within the same module.
 * @see com.marcusrunge.mydefcon.data.implementations.DataImpl
 */
internal abstract class DataBase(internal val context: Context?) : Data {
    /**
     * The backing field for the repository instance.
     *
     * This property is intended to be initialized by a subclass (e.g., `DataImpl`) and holds
     * the concrete implementation of the [Repository]. It is `protected` to allow access
     * only within this class and its subclasses.
     */
    protected lateinit var _repository: Repository

    /**
     * Provides access to the application's data repository.
     *
     * This property exposes the underlying [_repository] instance, providing a single
     * point of access to the data layer's repository.
     *
     * @see Repository
     */
    override val repository: Repository
        get() = _repository
}
