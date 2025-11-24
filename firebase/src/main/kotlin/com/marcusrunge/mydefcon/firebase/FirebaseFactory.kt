package com.marcusrunge.mydefcon.firebase

import android.content.Context
import com.marcusrunge.mydefcon.firebase.implementations.FirebaseImpl
import com.marcusrunge.mydefcon.firebase.interfaces.Firebase

interface FirebaseFactory {
    /**
     * Creates the firebase instance
     * @see Firebase
     * @param context The application context
     */
    fun create(context: Context?, core: Core?): Firebase
}

class FirebaseFactoryImpl {
    companion object : FirebaseFactory {
        private var instance: Firebase? = null
        override fun create(context: Context?, core: Core?): Firebase = when {
            instance != null -> instance!!
            else -> {
                instance = FirebaseImpl(context, core)
                instance!!
            }
        }
    }
}