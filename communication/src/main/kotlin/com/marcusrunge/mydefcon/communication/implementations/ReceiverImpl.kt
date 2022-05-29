package com.marcusrunge.mydefcon.communication.implementations

import com.marcusrunge.mydefcon.communication.bases.NetworkBase
import com.marcusrunge.mydefcon.communication.interfaces.OnCheckItemsReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnDefconStatusReceivedListener
import com.marcusrunge.mydefcon.communication.interfaces.OnReceived
import com.marcusrunge.mydefcon.communication.interfaces.Receiver
import com.marcusrunge.mydefcon.data.entities.CheckItem
import java.lang.ref.WeakReference

internal class ReceiverImpl(private val base: NetworkBase) : Receiver, OnReceived {
    private val onDefconStatusReceivedListeners: MutableList<WeakReference<OnDefconStatusReceivedListener>> =
        mutableListOf()
    private val onCheckItemsReceivedListeners: MutableList<WeakReference<OnCheckItemsReceivedListener>> =
        mutableListOf()

    internal companion object {
        private var instance: Receiver? = null
        fun create(base: NetworkBase): Receiver = when {
            instance != null -> instance!!
            else -> {
                instance = ReceiverImpl(base)
                instance!!
            }
        }
    }

    override fun addOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener) {
        onDefconStatusReceivedListeners.add(WeakReference(onDefconStatusReceivedListener))
    }

    override fun removeOnDefconStatusReceivedListener(onDefconStatusReceivedListener: OnDefconStatusReceivedListener) {
        val iterator: MutableIterator<WeakReference<OnDefconStatusReceivedListener>> =
            onDefconStatusReceivedListeners.iterator()
        while (iterator.hasNext()) {
            val weakRef: WeakReference<OnDefconStatusReceivedListener> = iterator.next()
            if (weakRef.get() === onDefconStatusReceivedListener) {
                iterator.remove()
            }
        }
    }

    override fun addOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener) {
        onCheckItemsReceivedListeners.add(WeakReference(onCheckItemsReceivedListener))
    }

    override fun removeOnCheckItemsReceivedListener(onCheckItemsReceivedListener: OnCheckItemsReceivedListener) {
        val iterator: MutableIterator<WeakReference<OnCheckItemsReceivedListener>> =
            onCheckItemsReceivedListeners.iterator()
        while (iterator.hasNext()) {
            val weakRef: WeakReference<OnCheckItemsReceivedListener> = iterator.next()
            if (weakRef.get() === onCheckItemsReceivedListener) {
                iterator.remove()
            }
        }
    }

    override fun onCheckItemsReceived(checkItems: List<CheckItem>) {
        for (weakRef in onCheckItemsReceivedListeners) {
            try {
                weakRef.get()?.onCheckItemsReceived(checkItems)
            } catch (e: Exception) {
            }
        }
    }

    override fun onDefconStatusReceived(status: Int) {
        for (weakRef in onDefconStatusReceivedListeners) {
            try {
                weakRef.get()?.onDefconStatusReceived(status)
            } catch (e: Exception) {
            }
        }
    }
}