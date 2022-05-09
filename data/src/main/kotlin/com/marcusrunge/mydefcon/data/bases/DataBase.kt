package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.data.interfaces.Repository

internal abstract class DataBase(internal val context: Context?) : Data {
    protected lateinit var _repository: Repository
    override val repository: Repository
        get() = _repository
}