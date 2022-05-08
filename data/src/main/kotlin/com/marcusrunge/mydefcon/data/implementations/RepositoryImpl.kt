package com.marcusrunge.mydefcon.data.implementations

import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.bases.RepositoryBase
import com.marcusrunge.mydefcon.data.interfaces.CheckItems
import com.marcusrunge.mydefcon.data.interfaces.Repository

internal class RepositoryImpl(dataBase: DataBase) : RepositoryBase(dataBase.context) {
    companion object {
        private var repository: Repository? = null
        fun create(dataBase: DataBase): Repository = when {
            repository != null -> repository!!
            else -> {
                repository = RepositoryImpl(dataBase)
                repository!!
            }
        }
    }

    init {
        _checkItems = myDefconDatabase?.checkItems()!!
    }

    override val checkItems: CheckItems
        get() = _checkItems
}