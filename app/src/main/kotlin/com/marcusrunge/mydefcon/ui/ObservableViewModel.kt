package com.marcusrunge.mydefcon.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel

/**
 * An abstract [AndroidViewModel] that implements the [Observable] interface, providing a base
 * for ViewModels that use data binding.
 *
 * This class simplifies the implementation of [Observable] by providing the necessary
 * callbacks and a [Handler] to update the UI from background threads.
 *
 * @param application The application instance provided by Hilt or the Android framework.
 */
abstract class ObservableViewModel(application: Application) : AndroidViewModel(application),
    Observable {

    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }

    /**
     * A [Handler] for processing messages on the main thread.
     *
     * Subclasses can use this handler to send messages from background threads to update the UI
     * by calling [updateView].
     */
    protected val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(inputMessage: Message) {
            if (inputMessage.what == UPDATE_VIEW) {
                updateView(inputMessage)
            }
        }
    }

    /**
     * This abstract method is called on the main thread when a message with the `what`
     * property set to [UPDATE_VIEW] is received by the [handler].
     *
     * @param inputMessage The [Message] containing the update information.
     */
    abstract fun updateView(inputMessage: Message)

    /**
     * Adds a callback to listen for property changes.
     *
     * @param callback The callback to add.
     */
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

    /**
     * Removes a callback previously added with [addOnPropertyChangedCallback].
     *
     * @param callback The callback to remove.
     */
    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.remove(callback)
    }

    /**
     * Notifies listeners that all properties of this instance have changed.
     */
    @Suppress("unused")
    fun notifyChange() {
        callbacks.notifyCallbacks(this, 0, null)
    }

    /**
     * Notifies listeners that a specific property has changed. The getter for the property
     * that changes should be marked with [Bindable] to generate a field in
     * `BR` to be used as `fieldId`.
     *
     * @param fieldId The generated BR id for the Bindable field.
     */
    fun notifyPropertyChanged(fieldId: Int) {
        callbacks.notifyCallbacks(this, fieldId, null)
    }

    companion object {
        /**
         * A constant used in [Message] objects to indicate that the view should be updated.
         */
        const val UPDATE_VIEW: Int = 1
    }
}
