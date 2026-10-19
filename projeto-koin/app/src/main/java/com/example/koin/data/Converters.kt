package com.example.koin.data

import androidx.room.TypeConverter
import java.util.Date

class Converters {

    @TypeConverter
    fun deTimestampParaDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun deDateParaTimestamp(date: Date?): Long? {
        return date?.time
    }
}
