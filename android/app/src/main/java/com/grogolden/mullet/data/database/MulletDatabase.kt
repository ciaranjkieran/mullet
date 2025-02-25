package com.grogolden.mullet.data.database

import android.content.Context

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.grogolden.mullet.data.local.entities.ModeEntity
import com.grogolden.mullet.data.local.entities.TaskEntity

/**
 * Room database for the Mullet app.
 *
 * - Manages local storage of tasks and modes.
 * - Provides DAOs for database access.
 * - Implements a singleton instance for database access.
 */
@Database(entities = [TaskEntity::class, ModeEntity::class], version = 1, exportSchema = false)
abstract class MulletDatabase : RoomDatabase() {

    /**
     * Provides DAO for task-related database operations.
     */
    abstract fun taskDao(): TaskDao

    /**
     * Provides DAO for mode-related database operations.
     */
    abstract fun modeDao(): ModeDao

    companion object {
        @Volatile
        private var INSTANCE: MulletDatabase? = null

        /**
         * Retrieves a singleton instance of the database.
         *
         * - Uses `synchronized` to ensure thread safety.
         * - Calls `fallbackToDestructiveMigration()` to recreate the DB on schema changes.
         *
         * @param context The application context.
         * @return The Room database instance.
         */
        fun getDatabase(context: Context): MulletDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MulletDatabase::class.java,
                    "mullet_database"
                )
                    .fallbackToDestructiveMigration() // Recreate DB on schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
