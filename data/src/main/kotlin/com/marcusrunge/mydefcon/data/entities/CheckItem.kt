package com.marcusrunge.mydefcon.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class CheckItem(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "text") var text: String?,
    @ColumnInfo(name = "is_checked") var isChecked: Boolean,
    @ColumnInfo(name = "is_deleted") var isDeleted: Boolean,
    @ColumnInfo(name = "defcon") var defcon: Int,
    @ColumnInfo(name = "created") val created: Long?,
    @ColumnInfo(name = "updated") var updated: Long
)