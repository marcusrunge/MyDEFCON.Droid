package com.marcusrunge.mydefcon.data.utils

import androidx.room.TypeConverter
import java.util.*

internal class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}