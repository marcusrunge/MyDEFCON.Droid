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
    val database =
        Firebase.database("https://mydefcon-d37fa-default-rtdb.europe-west1.firebasedatabase.app")
    var reference: DatabaseReference = database.reference
    override fun onDataChange(snapshot: DataSnapshot) {
        val createdGroupId = base.core?.preferences?.createdDefconGroupId
        val joinedGroupId = base.core?.preferences?.joinedDefconGroupId
        val groupId = if (!createdGroupId.isNullOrEmpty()) createdGroupId else joinedGroupId
        if (!groupId.isNullOrEmpty() && snapshot.hasChild(groupId)) {
            snapshot.child(groupId).getValue<Int>()?.let {
                base.core?.liveDataManager?.emitDefconStatus(it, this.javaClass)
            }
        }
    }

    override fun onCancelled(error: DatabaseError) {
    }

    override fun fetchDefconStatus(joinedDefconGroupId: String) {
        database.getReference(joinedDefconGroupId).get().addOnSuccessListener {
            it.getValue<Int>()?.let { status ->
                base.core?.preferences?.status = status
                base.core?.liveDataManager?.emitDefconStatus(status, this.javaClass)
            }
        }
    }

    init {
        reference.addValueEventListener(this)
        base.core?.coroutineScope?.launch {
            base.core.liveDataManager?.defconStatusFlow?.collect {
                if (it.second.name != "com.marcusrunge.mydefcon.MainActivity") {
                    val createdGroupId = base.core.preferences?.createdDefconGroupId
                    val joinedGroupId = base.core.preferences?.joinedDefconGroupId
                    val groupId =
                        if (!createdGroupId.isNullOrEmpty()) createdGroupId else joinedGroupId
                    if (!groupId.isNullOrEmpty() && it.second != RealtimeImpl::class.java) {
                        database.getReference(groupId).setValue(it.first)
                    }
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
