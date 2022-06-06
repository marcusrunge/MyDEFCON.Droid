package com.marcusrunge.mydefcon.communication

import android.content.Context
import com.marcusrunge.mydefcon.communication.implementations.CommunicationImpl
import com.marcusrunge.mydefcon.communication.interfaces.Communication
import com.marcusrunge.mydefcon.data.interfaces.Data

interface CommunicationFactory {
    /**
     * Creates the communication instance
     * @see Communication
     * @param context The application context
     * @param data The database
     */
    fun create(context: Context?, data: Data?): Communication
}

class CommunicationFactoryImpl {
    companion object : CommunicationFactory {
        private var communication: Communication? = null
        override fun create(context: Context?, data: Data?): Communication = when {
            communication != null -> communication!!
            else -> {
                communication = CommunicationImpl(context, data)
                communication!!
            }
        }
    }
}