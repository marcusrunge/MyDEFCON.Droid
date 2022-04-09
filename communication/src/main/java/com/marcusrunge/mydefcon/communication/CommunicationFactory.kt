package com.marcusrunge.mydefcon.communication

import android.content.Context
import com.marcusrunge.mydefcon.communication.implementations.CommunicationImpl
import com.marcusrunge.mydefcon.communication.interfaces.Communication

interface CommunicationFactory {
    /**
     * Creates the communication instance
     * @see Communication
     * @param context The application context
     */
    fun create(context: Context?): Communication
}

class CommunicationFactoryImpl {
    companion object : CommunicationFactory {
        private var communication: Communication? = null
        override fun create(context: Context?): Communication = when {
            communication != null -> communication!!
            else -> {
                communication = CommunicationImpl(context)
                communication!!
            }
        }
    }
}