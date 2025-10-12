package com.marcusrunge.mydefcon.utils

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface LiveDataManager {
    val intent: LiveData<Intent>
    fun sendIntent(intent: Intent)
}

internal class LiveDataManagerUtil(private val context: Context?) : LiveDataManager {
    private val _intent = MutableLiveData<Intent>()
    override val intent: LiveData<Intent>
        get() = _intent

    override fun sendIntent(intent: Intent) {
        _intent.postValue(intent)
    }

    internal companion object {
        private var instance: LiveDataManager? = null
        fun create(context: Context?): LiveDataManager = when {
            instance != null -> instance!!
            else -> {
                instance = LiveDataManagerUtil(context)
                instance!!
            }
        }
    }
}