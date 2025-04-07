package com.marcusrunge.mydefcon.data.implementations

import android.content.Context
import com.marcusrunge.mydefcon.data.bases.DataBase

internal class DataImpl(context: Context?) : DataBase(context) {
    init {
        _repository = RepositoryImpl.create(this)
        _firestore = FirestoreImpl.create(this)
    }
}