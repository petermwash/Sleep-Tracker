package com.pemwa.sleeptracker.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * A Dao Interface for the sleep database
 */
@Dao
interface SleepDatabaseDao {

    /**
     * A mapping function to insert a row into the database
     * @param night a SleepNight data object
     */
    @Insert
    fun insert(night: SleepNight)

    /**
     * A mapping function to update a row in the database
     * @param night a SleepNight data object
     */
    @Update
    fun update(night: SleepNight)

    /**
     * A mapping function to get all columns from the "daily_sleep_quality_table"
     * WHERE the nightId matches the :key argument.
     * @param key a unit id used to identify entities in our database table
     * @return SleepNight? a nullable SleepNight data object
     */
    @Query("SELECT * FROM daily_sleep_quality_table WHERE nightId = :key")
    fun get(key: Long) : SleepNight?

    /**
     * A mapping function to perform a delete of all entities in our table
     */
    @Query("DELETE FROM daily_sleep_quality_table")
    fun clear()

    /**
     * A mapping function that gets all the SleepNight data from our table ordered in a descending order
     * @return LiveData<List<SleepNight>> returns a list of SleepNight as LiveData
     * Room keeps this LiveData updated for us, and we don't have to specify an observer for it.
     */
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC")
    fun getAllNights(): LiveData<List<SleepNight>>

    /**
     * A mapping function that get the SleepNight data for tonight
     * @return SleepNight returns the latest SleepNight data object
     */
    @Query("SELECT * FROM daily_sleep_quality_table ORDER BY nightId DESC LIMIT 1")
    fun getTonight(): SleepNight?

    /**
     * Selects and returns the night with given nightId.
     */
    @Query("SELECT * FROM daily_sleep_quality_table WHERE nightId = :key")
    fun getNightWithId(key: Long): LiveData<SleepNight>

}