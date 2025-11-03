// com.example.travelvault.data.local/AppDatabase.kt
package com.example.travelvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelvault.data.model.ItineraryItem // <-- NEW IMPORT
import com.example.travelvault.data.model.Ticket

// --- UPDATED: Version is now 3, added ItineraryItem ---
@Database(
    entities = [Ticket::class, ItineraryItem::class], // <-- ADDED ItineraryItem::class
    version = 3, // <-- CHANGED FROM 2 to 3
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao
    abstract fun itineraryItemDao(): ItineraryItemDao // <-- ADDED new DAO

    companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE tickets ADD COLUMN fileMimeType TEXT NOT NULL DEFAULT 'application/pdf'"
                )
            }
        }

        // --- NEW MIGRATION (2 to 3) ---
        /**
         * Migration from version 2 to 3.
         * This creates the new 'itinerary_items' table.
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `itinerary_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `ticketId` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `time` INTEGER NOT NULL,
                        `title` TEXT NOT NULL,
                        `notes` TEXT,
                        `location` TEXT,
                        FOREIGN KEY(`ticketId`) REFERENCES `tickets`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                // Add an index for faster lookups
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_itinerary_items_ticketId` ON `itinerary_items` (`ticketId`)")
            }
        }
        // --- END NEW MIGRATION ---
    }
}

