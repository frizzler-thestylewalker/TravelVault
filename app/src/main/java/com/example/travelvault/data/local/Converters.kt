// com.example.travelvault.data.local/Converters.kt
package com.example.travelvault.data.local

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        // Convert milliseconds (Long) back to LocalDate
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        // Convert LocalDate to a Long (day number since epoch) for storage
        return date?.toEpochDay()
    }
}