package com.marcusrunge.mydefcon.communication.implementations

import android.content.Context
import com.marcusrunge.mydefcon.communication.bases.CommunicationBase

internal class CommunicationImpl(context: Context?) : CommunicationBase(context) {
    init {
        _network = NetworkImpl.create(this)
    }
}