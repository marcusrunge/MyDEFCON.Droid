package com.marcusrunge.mydefcon.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
@kotlinx.serialization.Serializable
data class CheckItem(
    @PrimaryKey(autoGenerate = true) var id: Long,
    @ColumnInfo(name = "uuid") var uuid: String,
    @ColumnInfo(name = "text") var text: String?,
    @ColumnInfo(name = "is_checked") var isChecked: Boolean,
    @ColumnInfo(name = "is_deleted") var isDeleted: Boolean,
    @ColumnInfo(name = "defcon") var defcon: Int,
    @ColumnInfo(name = "created") val created: Long?,
    @ColumnInfo(name = "updated") var updated: Long
) : Serializable