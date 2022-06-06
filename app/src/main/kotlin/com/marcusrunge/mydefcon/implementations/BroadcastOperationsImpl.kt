package com.marcusrunge.mydefcon.implementations

import android.content.Context
import android.content.Intent
import com.marcusrunge.mydefcon.MyDefconWidget
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import javax.inject.Inject

internal class BroadcastOperationsImpl @Inject constructor(private val context: Context?) :
    BroadcastOperations {
    override fun sendBroadcast(action: String?, data: String?) {
        Intent(context, MyDefconWidget::class.java).also { intent ->
            intent.action = action
            intent.putExtra("data", data)
            context?.sendBroadcast(intent)
        }
    }
}