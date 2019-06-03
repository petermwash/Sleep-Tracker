package com.pemwa.sleeptracker.sleepquality


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.pemwa.sleeptracker.R
import com.pemwa.sleeptracker.database.SleepDatabase
import com.pemwa.sleeptracker.databinding.FragmentSleepQualityBinding

/**
 * Fragment that displays a list of clickable icons,
 * each representing a sleep quality rating.
 * Once the user taps an icon, the quality is set in the current sleepNight
 * and the database is updated.
 */
class SleepQualityFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepQualityBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_quality, container, false)

        val application = requireNotNull(this.activity).application

        // Getting the argument from SleepTracker
        val arguments = SleepQualityFragmentArgs.fromBundle(arguments!!)

        // Getting the dataSource
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepQualityViewModelFactory(arguments.sleepNightKey, dataSource)
        val  viewModel = ViewModelProviders.of(this, viewModelFactory).get(SleepQualityViewModel::class.java)

        binding.sleepQualityViewModel = viewModel

        viewModel.navigateToSleepTracker.observe(this,  Observer {
            if (it == true) { // Observed state is true.
                this.findNavController().navigate(
                    SleepQualityFragmentDirections.actionSleepQualityFragmentToSleepTrackerFragment())
                viewModel.onDoneNavigating()
            }
        })


        return binding.root
    }
}
