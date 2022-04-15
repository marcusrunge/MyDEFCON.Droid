package com.marcusrunge.mydefcon.data.implementations

import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.interfaces.Settings

internal class SettingsImpl(dataBase: DataBase) : Settings {
    companion object {
        private var settings: Settings? = null
        fun create(dataBase: DataBase): Settings = when {
            settings != null -> settings!!
            else -> {
                settings = SettingsImpl(dataBase)
                settings!!
            }
        }
    }
}