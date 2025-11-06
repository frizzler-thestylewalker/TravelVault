// com.example.travelvault.data.local/AppDatabase.kt
package com.example.travelvault.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.travelvault.data.model.ItineraryFolderEntity
import com.example.travelvault.data.model.ItineraryItemEntity
import com.example.travelvault.data.model.Ticket

// --- UPDATED: Version is now 4 ---
@Database(
    entities = [
        Ticket::class,
        ItineraryFolderEntity::class,
        ItineraryItemEntity::class
    ],
    version = 4, // <-- CHANGED FROM 3 to 4
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao
    abstract fun itineraryDao(): ItineraryDao

    companion object {
        /**
         * Migration from version 1 to 2 (Existing).
         */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE tickets ADD COLUMN fileMimeType TEXT NOT NULL DEFAULT 'application/pdf'"
                )
            }
        }

        /**
         * Migration from version 2 to 3 (Existing).
         */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Create the 'itinerary_folders' table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `itinerary_folders` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `title` TEXT NOT NULL,
                        `description` TEXT,
                        `createdAt` INTEGER NOT NULL
                    )
                """)

                // 2. Create the 'itinerary_items' table
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `itinerary_items` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `folderId` INTEGER NOT NULL,
                        `date` INTEGER NOT NULL,
                        `time` TEXT,
                        `title` TEXT NOT NULL,
                        `notes` TEXT,
                        FOREIGN KEY(`folderId`) REFERENCES `itinerary_folders`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                """)

                // 3. Create the index
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_itinerary_items_folderId` ON `itinerary_items` (`folderId`)")
            }
        }

        // --- NEW: Define the migration from 3 to 4 ---
        /**
         * Migration from version 3 to 4.
         * Adds the 'transportType' column to the 'tickets' table.
         */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE tickets ADD COLUMN transportType TEXT NOT NULL DEFAULT 'OTHER'"
                )
            }
        }
        // --- END NEW ---
    }
}