package com.marcusrunge.mydefcon.communication.implementations

import android.content.Context
import com.marcusrunge.mydefcon.communication.bases.CommunicationBase
import com.marcusrunge.mydefcon.data.interfaces.Data

internal class CommunicationImpl(context: Context?, data: Data?) :
    CommunicationBase(context, data) {
    init {
        _network = NetworkImpl.create(this)
    }
}