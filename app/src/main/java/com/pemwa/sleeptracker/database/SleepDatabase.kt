package com.pemwa.sleeptracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Creating an instance of a RoomDatabase and declare what entity to use and the version
 */
@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    /**
     * Telling the database about the Dao associated with our entity
     */
    abstract val sleepDatabaseDao: SleepDatabaseDao

    /**
     * A companion object to allow clients to access the methods
     * for creating or getting the database without instantiating the class
     */
    companion object {

        /**
         * The INSTANCE variable will keep a reference to the database once created
         * this helps in avoiding to repeatedly create connections to the db since its an expensive operation
         *
         * The "@Volatile" annotation ensures that the value of Instance is up to date and same to all execution threads
         * This is because the value of a Volatile variable will never be cashed and all read/write are done to/from the main memory
         * It means that changes made in one thread to INSTANCE are visible to all other threads immediately
         */
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        /**
         * Returns a reference to the database
         */
        fun getInstance(context: Context): SleepDatabase {

            /**
             * Here we check whether the database already exists
             * If it does not, then we create one using "Room.databaseBuilder()"
             * Then we return the database INSTANCE
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_database")
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}