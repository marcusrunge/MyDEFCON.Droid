package com.marcusrunge.mydefcon.firebase.implementations

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.database.getValue
import com.marcusrunge.mydefcon.firebase.bases.FirebaseBase
import com.marcusrunge.mydefcon.firebase.interfaces.Realtime
import kotlinx.coroutines.launch

internal class RealtimeImpl(private val base: FirebaseBase) : Realtime, ValueEventListener {
    private val tag: String = "RealtimeImpl"
    val database = Firebase.database
    var reference: DatabaseReference = database.reference
    override fun onDataChange(snapshot: DataSnapshot) {
        base.core?.liveDataManager?.emitDefconStatus(snapshot.getValue<Int>()!!, this.javaClass)
    }

    override fun onCancelled(error: DatabaseError) {
    }

    init {
        reference.addValueEventListener(this)
        base.core?.coroutineScope?.launch {
            base.core.liveDataManager?.defconStatusFlow?.collect {
                val groupId = base.core.preferences?.createdDefconGroupId
                if (!groupId.isNullOrEmpty() && it.second != RealtimeImpl::class.java) {
                    reference.child(groupId).setValue(it.first)
                }
            }
        }
    }

    internal companion object {
        private var instance: Realtime? = null
        fun create(base: FirebaseBase): Realtime = when {
            instance != null -> instance!!
            else -> {
                instance = RealtimeImpl(base)
                instance!!
            }
        }
    }
}
