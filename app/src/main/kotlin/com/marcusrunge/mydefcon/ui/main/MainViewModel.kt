package com.marcusrunge.mydefcon.ui.main

import android.app.Application
import android.graphics.drawable.Drawable
import android.os.Message
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the main activity.
 *
 * This class is responsible for preparing and managing the data for the [MainActivity].
 * It handles business logic and state management for the main screen.
 *
 * @param application The application instance.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : ObservableViewModel(application) {

    private val _warningPattern = MutableLiveData<Drawable>()
    /**
     * A [Drawable] representing a warning pattern.
     * This is used to display a warning overlay on the main screen.
     */
    val warningPattern: LiveData<Drawable> = _warningPattern

    init {
        _warningPattern.value = AppCompatResources.getDrawable(
            application.applicationContext,
            R.drawable.ic_warning_pattern
        )
    }

    /**
     * Updates the UI based on messages from background threads. (Not yet implemented)
     * @param inputMessage The message containing update information.
     */
    override fun updateView(inputMessage: Message) {
        // TODO: Not yet implemented
    }
}
