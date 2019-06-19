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
         * Helper function to get the database.
         *
         * If a database has already been retrieved, the previous database will be returned.
         * Otherwise, create a new database.
         *
         * This function is threadsafe, and callers should cache the result for multiple database
         * calls to avoid overhead.
         *
         * This is an example of a simple Singleton pattern that takes another Singleton as an
         * argument in Kotlin.
         *
         * @param context The application context Singleton, used to get access to the filesystem.
         */
        fun getInstance(context: Context): SleepDatabase {

            /**
             *  Multiple threads can ask for the database at the same time, ensure we only initialize
             * it once by using synchronized. Only one thread may enter a synchronized block at a
             * time.
             * Here we check whether the database already exists
             * If it does not, then we create one using "Room.databaseBuilder()"
             * Then we return the database INSTANCE
             */
            synchronized(this) {
                // Copy the current value of INSTANCE to a local variable so Kotlin can smart cast.
                // Smart cast is only available to local variables.
                var instance = INSTANCE

                // If instance is `null` make a new database instance.
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        SleepDatabase::class.java,
                        "sleep_database")
                        // Wipes and rebuilds instead of migrating if no Migration object.
                        // https://medium.com/androiddevelopers/understanding-migrations-with-room-f01e04b07929
                        .fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                // Return instance; smart cast to be non-null.
                return instance
            }
        }
    }
}