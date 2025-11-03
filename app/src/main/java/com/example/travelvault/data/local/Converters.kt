// com.example.travelvault.data.local/Converters.kt
package com.example.travelvault.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime // <-- ADDED IMPORT

class Converters {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? {
        // Convert milliseconds (Long) back to LocalDate
        return value?.let { LocalDate.ofEpochDay(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        // Convert LocalDate to a Long (day number since epoch) for storage
        return date?.toEpochDay()
    }

    // --- ADDED THESE NEW FUNCTIONS ---
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromTime(value: Int?): LocalTime? {
        // Converts nanos-of-day (Int) back to LocalTime
        // Note: toNanoOfDay returns a Long, but fits in an Int for a 24-hour day
        return value?.let { LocalTime.ofNanoOfDay(it.toLong()) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toTime(time: LocalTime?): Int? {
        // Converts LocalTime to nanos-of-day (saved as Int)
        return time?.toNanoOfDay()?.toInt()
    }
    // --- END OF NEW FUNCTIONS ---
}

