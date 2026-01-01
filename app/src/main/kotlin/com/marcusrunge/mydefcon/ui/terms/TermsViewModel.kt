package com.marcusrunge.mydefcon.ui.terms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcusrunge.mydefcon.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * ViewModel for the terms of service screen.
 *
 * This class provides the URL for the terms of service to be displayed in a WebView.
 * It is injected with the application context to access string resources.
 *
 * @param context The application context provided by Hilt.
 */
@HiltViewModel
class TermsViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {

    /**
     * Backing property for the terms of service URL.
     * It is initialized with the URL string from the resources.
     */
    private val _endpointUrl = MutableLiveData(context.getString(R.string.html_terms))

    /**
     * The [LiveData] holding the URL of the terms of service.
     * The UI observes this property to load the correct web page.
     */
    val endpointUrl: LiveData<String> = _endpointUrl
}
