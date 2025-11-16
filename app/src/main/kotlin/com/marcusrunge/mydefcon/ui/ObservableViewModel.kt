package com.marcusrunge.mydefcon.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.databinding.Bindable
import androidx.databinding.Observable
import androidx.databinding.PropertyChangeRegistry
import androidx.lifecycle.AndroidViewModel

abstract class ObservableViewModel(application: Application) : AndroidViewModel(application),
    Observable {
    companion object {
        const val UPDATE_VIEW: Int = 1
    }

    lateinit var defconStatusButton: Int?
    private val callbacks: PropertyChangeRegistry by lazy { PropertyChangeRegistry() }
    protected val handler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(inputMessage: Message) {
            when (inputMessage.what) {
                UPDATE_VIEW -> updateView(inputMessage)
            }
        }
    }

    abstract fun updateView(inputMessage: Message)
    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback) {
        callbacks.add(callback)
    }

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
}