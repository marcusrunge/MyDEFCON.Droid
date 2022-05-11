package com.marcusrunge.mydefcon.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

class CheckItem() : ViewModel() {
    var id:Int = 0
    val text: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
    val isChecked: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val isDeleted: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val created: MutableLiveData<Date> by lazy {
        MutableLiveData<Date>()
    }
    val updated: MutableLiveData<Date> by lazy {
        MutableLiveData<Date>()
    }
}