package com.marcusrunge.mydefcon.communication.bases

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import com.marcusrunge.mydefcon.communication.interfaces.Client
import com.marcusrunge.mydefcon.communication.interfaces.Network
import com.marcusrunge.mydefcon.communication.interfaces.Server


internal abstract class NetworkBase(internal val context: Context?) : Network {
    protected lateinit var _server: Server
    protected lateinit var _client: Client
    override val server: Server
        get() = _server
    override val client: Client
        get() = _client
    internal var defconStatusMessageUuid: String? = null
    internal var checkItemsMessageUuid: String? = null
    internal var requestMessageUuid: String? = null

    internal val linkProperties: LinkProperties?
        @SuppressLint("MissingPermission")
        get() {
            val connectivityManager = context?.getSystemService(ConnectivityManager::class.java)
            val currentNetwork = connectivityManager?.activeNetwork
            return connectivityManager?.getLinkProperties(currentNetwork)
        }
}