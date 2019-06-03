package com.pemwa.sleeptracker.sleepquality

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pemwa.sleeptracker.database.SleepDatabaseDao
import kotlinx.coroutines.*

class SleepQualityViewModel(
    private val sleepNightKey: Long = 0L,
    val database: SleepDatabaseDao
) : ViewModel() {

    //
    private val _navigateToSleepTracker = MutableLiveData<Boolean>()
    val navigateToSleepTracker : LiveData<Boolean>
        get() = _navigateToSleepTracker

    // Defining a Job
    private val viewModelJob = Job()

    // Defining a coroutine scope
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /**
     * Setting the sleep quality data
     */
    fun onSetSleepQuality(quality: Int) {
        uiScope.launch {

            withContext(Dispatchers.IO) {
                val tonight = database.get(sleepNightKey) ?: return@withContext
                tonight.sleepQuality = quality
                database.update(tonight)
            }

            // navigating to SleepTracker
            _navigateToSleepTracker.value = true
        }
    }

    /**
     * Resets navigation event
     */
    fun onDoneNavigating() {
        _navigateToSleepTracker.value = null
    }


    override fun onCleared() {
        super.onCleared()

        // Cancel all the couritines on onCleared
        viewModelJob.cancel()
    }
}