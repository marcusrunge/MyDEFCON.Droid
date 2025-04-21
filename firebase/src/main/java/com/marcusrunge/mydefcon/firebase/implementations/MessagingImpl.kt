package com.marcusrunge.mydefcon.firebase.implementations

import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.interfaces.Messaging

internal class MessagingImpl(private val base: FirebaseBase) : Messaging {
    internal companion object {
        private var instance: Messaging? = null
        fun create(base: FirebaseBase): Messaging = when {
            instance != null -> instance!!
            else -> {
                instance = MessagingImpl(base)
                instance!!
            }
        }
    }
}