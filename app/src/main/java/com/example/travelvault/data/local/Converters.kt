// com.example.travelvault.data.local/Converters.kt
package com.example.travelvault.data.local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.travelvault.data.model.TransportType
import java.time.LocalDate

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

    // --- NEW CONVERTERS FOR TRANSPORT TYPE ---

    @TypeConverter
    fun fromTransportTypeToString(type: TransportType?): String? {
        // Convert our TransportType enum to a String (its name) for storage
        return type?.name
    }

    @TypeConverter
    fun fromStringToTransportType(value: String?): TransportType? {
        // Convert the String from the database back into our enum
        // Default to OTHER if the value is null or doesn't match
        return value?.let {
            try {
                TransportType.valueOf(it)
            } catch (e: IllegalArgumentException) {
                TransportType.OTHER // Handle old or invalid data
            }
        } ?: TransportType.OTHER
    }
    // --- END NEW CONVERTERS ---
}