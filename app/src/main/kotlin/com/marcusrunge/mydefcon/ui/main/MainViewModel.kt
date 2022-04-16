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

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : ObservableViewModel(application) {
    private val _warningPattern = MutableLiveData<Drawable>()

    init {
        _warningPattern.value = AppCompatResources.getDrawable(
            application.applicationContext,
            R.drawable.ic_warning_pattern
        )
    }

    val warningPattern: LiveData<Drawable> = _warningPattern

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }
}