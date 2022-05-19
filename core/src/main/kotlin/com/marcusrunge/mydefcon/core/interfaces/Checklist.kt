package com.marcusrunge.mydefcon.core.interfaces

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface Checklist {
    val defcon1ItemsCount: LiveData<String>
    val defcon2ItemsCount: LiveData<String>
    val defcon3ItemsCount: LiveData<String>
    val defcon4ItemsCount: LiveData<String>
    val defcon5ItemsCount: LiveData<String>
    val selectedChecklist: LiveData<Int>
}