package com.marcusrunge.mydefcon.communication.implementations

import android.content.Context
import com.marcusrunge.mydefcon.communication.bases.CommunicationBase
import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.Network
import com.marcusrunge.mydefcon.data.interfaces.Data

internal class NetworkImpl(context: Context?, data: Data?) : NetworkBase(context, data) {

    init {
        _server = ServerImpl.create(this)
        _client = ClientImpl.create(this)
    }

    internal companion object {
        const val CHECKITEMSSYNC_REQUESTCODE = 1
        private var instance: Network? = null
        fun create(base: CommunicationBase): Network = when {
            instance != null -> instance!!
            else -> {
                instance = NetworkImpl(base.context, base.data)
                instance!!
            }
        }
    }
}