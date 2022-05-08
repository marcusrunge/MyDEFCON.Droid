package com.marcusrunge.mydefcon.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class CheckItem(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "text") var text: String?,
    @ColumnInfo(name = "is_checked") var isChecked: Boolean?,
    @ColumnInfo(name = "is_deleted") var isDeleted: Boolean?,
    @ColumnInfo(name = "created") val created: Date?,
    @ColumnInfo(name = "updated") var updated: Date?
)