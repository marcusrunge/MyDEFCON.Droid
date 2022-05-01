package com.marcusrunge.mydefcon.ui.terms

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.marcusrunge.mydefcon.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor(@ApplicationContext context: Context) : ViewModel() {
    private val _endpointUrl = MutableLiveData(context.getString(R.string.html_terms))

    val endpointUrl: LiveData<String> = _endpointUrl
}