package com.marcusrunge.mydefcon.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class CheckItem (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "text") val text: String?,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean?,
    @ColumnInfo(name = "is_deleted") val isDeleted: Boolean?,
    @ColumnInfo(name = "created") val created: Date?,
    @ColumnInfo(name = "updated") val updated: Date?
)