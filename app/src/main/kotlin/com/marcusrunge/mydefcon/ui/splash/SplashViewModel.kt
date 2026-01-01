package com.marcusrunge.mydefcon.ui.splash

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
 * ViewModel for the splash screen.
 *
 * This class is responsible for preparing and managing the data for the splash screen UI.
 * It provides a warning pattern drawable to be displayed.
 *
 * @param application The application instance provided by Hilt.
 */
@HiltViewModel
class SplashViewModel @Inject constructor(
    application: Application
) : ObservableViewModel(application) {

    private val _warningPattern = MutableLiveData<Drawable>()
    /**
     * The [LiveData] holding the warning pattern drawable.
     * The UI observes this to display a warning overlay.
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
