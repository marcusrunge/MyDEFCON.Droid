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
    application: Application, core: Core
) : ObservableViewModel(application) {
    private val _checkedRadioButtonId = MutableLiveData<Int>()

    init {
        setDefconStatus(core.preferences.status)
    }

    val checkedRadioButtonId: MutableLiveData<Int> = _checkedRadioButtonId

    override fun updateView(inputMessage: Message) {
        if (inputMessage.obj is Int) setDefconStatus(
            inputMessage.obj as Int
        )
    }

    private fun setDefconStatus(status: Int) {
        when (status) {
            1 -> _checkedRadioButtonId.value = R.id.radio_defcon1
            2 -> _checkedRadioButtonId.value = R.id.radio_defcon2
            3 -> _checkedRadioButtonId.value = R.id.radio_defcon3
            4 -> _checkedRadioButtonId.value = R.id.radio_defcon4
            else -> _checkedRadioButtonId.value = R.id.radio_defcon5
        }
    }
}