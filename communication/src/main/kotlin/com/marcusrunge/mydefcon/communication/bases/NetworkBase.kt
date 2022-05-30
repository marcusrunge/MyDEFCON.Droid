package com.marcusrunge.mydefcon.communication.bases

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import com.marcusrunge.mydefcon.communication.interfaces.Network
import com.marcusrunge.mydefcon.communication.interfaces.Receiver
import com.marcusrunge.mydefcon.communication.interfaces.Sender


internal abstract class NetworkBase(internal val context: Context?) : Network {
    protected lateinit var _receiver: Receiver
    protected lateinit var _sender: Sender
    override val receiver: Receiver
        get() = _receiver
    override val sender: Sender
        get() = _sender
    internal var defconStatusMessageUuid: String? = null
    internal var checkItemsMessageUuid: String? = null

    internal val linkProperties: LinkProperties?
        @SuppressLint("MissingPermission")
        get() {
            val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
            val currentNetwork = connectivityManager?.activeNetwork
            return connectivityManager?.getLinkProperties(currentNetwork)
        }
}