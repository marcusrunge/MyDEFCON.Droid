package com.marcusrunge.mydefcon.communication.bases

import android.content.Context
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.communication.interfaces.Network
import com.marcusrunge.mydefcon.data.interfaces.Data

internal abstract class CommunicationBase(
    internal val context: Context?,
    internal val data: Data?
) : Communication {
    protected lateinit var _network: Network
    override val network: Network
        get() = _network
}