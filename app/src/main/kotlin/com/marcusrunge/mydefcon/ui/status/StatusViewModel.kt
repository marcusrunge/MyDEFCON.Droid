package com.marcusrunge.mydefcon.ui.status

import android.app.Application
import android.os.Message
import androidx.lifecycle.MutableLiveData
import com.marcusrunge.mydefcon.R
import com.marcusrunge.mydefcon.core.interfaces.Core
import com.marcusrunge.mydefcon.ui.ObservableViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatusViewModel @Inject constructor(
    application: Application, private val core: Core
) : ObservableViewModel(application) {
    private val _checkedRadioButtonId = MutableLiveData<Int>()

    init {
        when (core.preferences.status) {
            1 -> _checkedRadioButtonId.value = R.id.radio_defcon1
            2 -> _checkedRadioButtonId.value = R.id.radio_defcon2
            3 -> _checkedRadioButtonId.value = R.id.radio_defcon3
            4 -> _checkedRadioButtonId.value = R.id.radio_defcon4
            else -> _checkedRadioButtonId.value = R.id.radio_defcon5
        }
    }

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId

    fun onCheckedChanged() {
        when (_checkedRadioButtonId.value) {
            R.id.radio_defcon1 -> core.preferences.status = 1
            R.id.radio_defcon2 -> core.preferences.status = 2
            R.id.radio_defcon3 -> core.preferences.status = 3
            R.id.radio_defcon4 -> core.preferences.status = 4
            R.id.radio_defcon5 -> core.preferences.status = 5
        }
    }

    override fun updateView(inputMessage: Message) {
        TODO("Not yet implemented")
    }
}