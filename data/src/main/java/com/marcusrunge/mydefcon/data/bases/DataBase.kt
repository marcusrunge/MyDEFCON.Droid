package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.data.interfaces.Repository
import com.marcusrunge.mydefcon.data.interfaces.Settings

internal abstract class DataBase(internal val context: Context?) : Data {
    protected lateinit var _repository: Repository
    protected lateinit var _settings: Settings
}