package com.marcusrunge.mydefcon.core.implementations

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.marcusrunge.mydefcon.core.bases.CoreBase
import com.marcusrunge.mydefcon.core.interfaces.Checklist

internal class ChecklistImpl(private val coreBase: CoreBase) : Checklist {
    private val _defcon1ItemsCount= MutableLiveData<String>()
    private val _defcon2ItemsCount= MutableLiveData<String>()
    private val _defcon3ItemsCount= MutableLiveData<String>()
    private val _defcon4ItemsCount= MutableLiveData<String>()
    private val _defcon5ItemsCount= MutableLiveData<String>()
    private val _selectedChecklist= MutableLiveData<Int>()
    override val defcon1ItemsCount: LiveData<String>
        get() = _defcon1ItemsCount
    override val defcon2ItemsCount: LiveData<String>
        get() = _defcon2ItemsCount
    override val defcon3ItemsCount: LiveData<String>
        get() = _defcon3ItemsCount
    override val defcon4ItemsCount: LiveData<String>
        get() = _defcon4ItemsCount
    override val defcon5ItemsCount: LiveData<String>
        get() = _defcon5ItemsCount
    override val selectedChecklist: LiveData<Int>
        get() = _selectedChecklist

    internal companion object {
        var checklist: Checklist? = null
        fun create(coreBase: CoreBase): Checklist = when {
            checklist != null -> checklist!!
            else -> {
                checklist = ChecklistImpl(coreBase)
                checklist!!
            }
        }
    }
}