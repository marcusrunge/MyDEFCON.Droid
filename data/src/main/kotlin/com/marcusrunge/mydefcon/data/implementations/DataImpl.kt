package com.marcusrunge.mydefcon.data.implementations

import android.content.Context
import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.interfaces.Repository
import com.marcusrunge.mydefcon.data.interfaces.Settings

internal class DataImpl(context: Context?) : DataBase(context) {
    init {
        _repository = RepositoryImpl.create(this)
        _settings = SettingsImpl.create(this)
    }

    override val repository: Repository
        get() = _repository
    override val settings: Settings
        get() = _settings
}