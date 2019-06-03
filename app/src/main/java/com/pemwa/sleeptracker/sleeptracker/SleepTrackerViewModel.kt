package com.pemwa.sleeptracker.sleeptracker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pemwa.sleeptracker.database.SleepDatabaseDao
import com.pemwa.sleeptracker.database.SleepNight
import com.pemwa.sleeptracker.util.formatNights
import kotlinx.coroutines.*

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    //
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    //
    private var _showSnackbarEvent = MutableLiveData<Boolean>()
    val showSnackBarEvent: LiveData<Boolean>
        get() = _showSnackbarEvent

    // Defining a job that we use to manage all our coroutines
    private val viewModelJob  = Job()

    // Defining a scope for the coroutines to run in
    // We use the scope to determine which thread the coroutine will run on and it has to know about the job
    private val  uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    // Creating a tonight live data var and use a coroutine to initialize it from the database
    private  var tonight = MutableLiveData<SleepNight?>()

    // Getting all nights from the database
    private val nights = database.getAllNights()

    // The START button should be visible when tonight is null
    val startButtonVisible = Transformations.map(tonight) {
        null == it
    }

    // The STOP button should be visible when tonight is not null
    val stopButtonVisible = Transformations.map(tonight) {
        null != it
    }

    // The CLEAR button should be visible if nights contains any night(s)
    val clearButtonVisible = Transformations.map(nights) {
        it?.isNotEmpty()
    }

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        uiScope.launch {
            tonight.value = getTonightFromDb()
        }
    }

    private suspend fun getTonightFromDb(): SleepNight? {
        return withContext(Dispatchers.IO) {
            var night = database.getTonight()

            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        }
    }

    // Adding local functions for insert(), update(), and clear()
    private suspend fun insert(newNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.insert(newNight)
        }
    }

    // Implementing click handlers for start, stop, and clear buttons
    // using coroutine to do the database work

    fun onStartTracking() {
        uiScope.launch {
            val newNight = SleepNight()
            insert(newNight)
            tonight.value = getTonightFromDb()
        }
    }

    fun onStopTracking() {
        uiScope.launch {
            val oldNight = tonight.value ?: return@launch
            oldNight.endTimeMilli = System.currentTimeMillis()
            update(oldNight)

            // Triggering the navigation
            _navigateToSleepQuality.value = oldNight
        }
    }

    private suspend fun update(oldNight: SleepNight) {
        withContext(Dispatchers.IO) {
            database.update(oldNight)
        }
    }

    fun onClear() {
        uiScope.launch {
            clear()
            tonight.value = null
            _showSnackbarEvent.value = true
        }
    }

    private suspend fun clear() {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    // Transform nights into a nightString using formatNights()
    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    /**
     * Rests navigation event
     */
    fun onDoneNavigating() {
        _navigateToSleepQuality.value = null
    }

    /**
     * Resets showing snackBar event
     */
    fun onDoneShowingSnackbar() {
        _showSnackbarEvent.value = false
    }

    /**
     * Cancel the scope/job when ViewModel is cleared
     * Since viewModelJob is passed as the job to uiScope,
     * when viewModelJob is cancelled every coroutine started by uiScope will be cancelled as well.
     */
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
