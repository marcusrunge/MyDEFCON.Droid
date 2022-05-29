package com.marcusrunge.mydefcon.communication.bases

import android.content.Context
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.communication.interfaces.Network

internal abstract class CommunicationBase(internal val context: Context?) : Communication {
    protected lateinit var _network: Network
    override val network: Network
        get() = _network
}