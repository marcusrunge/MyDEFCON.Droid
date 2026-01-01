package com.marcusrunge.mydefcon.implementations

import android.content.Context
import android.content.Intent
import com.marcusrunge.mydefcon.MyDefconWidget
import com.marcusrunge.mydefcon.core.interfaces.BroadcastOperations
import javax.inject.Inject

/**
 * Implements [BroadcastOperations] to send broadcasts to the [MyDefconWidget].
 *
 * @param context The application context provided by Hilt.
 */
internal class BroadcastOperationsImpl @Inject constructor(private val context: Context?) :
    BroadcastOperations {

    /**
     * Sends a broadcast with a specified action and data to the [MyDefconWidget].
     *
     * The intent is created explicitly for the [MyDefconWidget] class. The provided `action`
     * and `data` are added to the intent before it's sent as a system-wide broadcast.
     * The data is passed as an extra with the key "data".
     *
     * @param action An optional string representing the intent action.
     * @param data An optional string containing data to be passed to the widget.
     */
    override fun sendBroadcastToMyDefconWidget(action: String?, data: String?) {
        Intent(context, MyDefconWidget::class.java).also { intent ->
            intent.action = action
            intent.putExtra("data", data)
            context?.sendBroadcast(intent)
        }
    }
}
