package com.marcusrunge.mydefcon.data.bases

import android.content.Context
import com.marcusrunge.mydefcon.data.interfaces.Data
import com.marcusrunge.mydefcon.data.interfaces.Firestore
import com.marcusrunge.mydefcon.data.interfaces.Repository

internal abstract class DataBase(internal val context: Context?) : Data {
    protected lateinit var _repository: Repository
    protected lateinit var _firestore: Firestore
    override val repository: Repository
        get() = _repository
    override val firestore: Firestore
        get() = _firestore
}