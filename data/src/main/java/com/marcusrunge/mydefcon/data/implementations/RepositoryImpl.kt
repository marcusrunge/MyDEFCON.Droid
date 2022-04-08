package com.marcusrunge.mydefcon.data.implementations

import com.marcusrunge.mydefcon.data.bases.DataBase
import com.marcusrunge.mydefcon.data.interfaces.Repository

internal class RepositoryImpl(dataBase: DataBase) : Repository {
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
}