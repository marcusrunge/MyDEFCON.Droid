package com.marcusrunge.mydefcon.communication.implementations

import android.content.Context
import com.marcusrunge.mydefcon.communication.bases.CommunicationBase
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Network

internal class NetworkImpl(context: Context?) : NetworkBase(context) {
    init {
        _receiver = ReceiverImpl.create(this)
        _sender = SenderImpl.create(this)
    }

    internal companion object {
        private var instance: Network? = null
        fun create(base: CommunicationBase): Network = when {
            instance != null -> instance!!
            else -> {
                instance = NetworkImpl(base.context)
                instance!!
            }
        }
    }
}