// com.example.travelvault.data.local/AppDatabase.kt
package com.example.travelvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelvault.data.model.Ticket

// --- UPDATED: Version is now 2 ---
@Database(
    entities = [Ticket::class],
    version = 2, // <-- CHANGED FROM 1 to 2
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao

    // --- NEW: Define the migration ---
    companion object {
        /**
         * A simple migration from version 1 to 2.
         * This tells Room to:
         * 1. Add a new column "fileMimeType" of type TEXT.
         * 2. Make it non-null.
         * 3. Give all *existing* rows a default value of "application/pdf"
         * (since all old tickets were PDFs).
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE tickets ADD COLUMN fileMimeType TEXT NOT NULL DEFAULT 'application/pdf'"
                )
            }
        }
    }
    // --- END NEW ---
}