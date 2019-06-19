package com.pemwa.sleeptracker.sleeptracker


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar

import com.pemwa.sleeptracker.R
import com.pemwa.sleeptracker.database.SleepDatabase
import com.pemwa.sleeptracker.databinding.FragmentSleepTrackerBinding

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 */
class SleepTrackerFragment : Fragment() {

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Getting a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sleep_tracker, container, false)

        // Getting a reference to application context
        val application = requireNotNull(this.activity).application

        // Getting a reference to the DAO of the database
        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        // Creating an instance of the ViewModelFactory and use it to get a reference to the SleepTrackerViewModel
        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)
        val viewModel = ViewModelProviders.of(this, viewModelFactory).get(SleepTrackerViewModel::class.java)

        // An observer to navigate to to SleepQualityFragment
        viewModel.navigateToSleepQuality.observe(this, Observer {  night ->
            night?.let {
                this.findNavController().navigate(
                    SleepTrackerFragmentDirections
                        .actionSleepTrackerFragmentToSleepQualityFragment(night.nightId)
                )
                viewModel.onDoneNavigating()
            }
        })

        // An observer for showing snackBar when data is cleared
        viewModel.showSnackBarEvent.observe(this, Observer {
            if (it == true) { // Observe state is true
                Snackbar.make(
                    activity!!.findViewById(android.R.id.content),
                    getString(R.string.cleared_message),
                    Snackbar.LENGTH_LONG
                ).show()
                viewModel.onDoneShowingSnackbar()
            }
        })

        // An observer to trigger navigation when the listener passes the data to viewModel
        viewModel.navigateToSleepDataQuality.observe(this, Observer { night ->
            night?.let {
                this.findNavController().navigate(SleepTrackerFragmentDirections
                    .actionSleepTrackerFragmentToSleepDetailFragment(night))
                viewModel.onSleepDataQualityNavigated()
            }
        })

        // Associate our adapter with recyclerView
        val adapter = SleepNightAdapter(SleepNightListener { nightId ->
            viewModel.onSleepNightClicked(nightId)
        })
        binding.sleepList.adapter = adapter

        // Observe nights on the viewModel and set the adapter data when there is any changes
        viewModel.nights.observe(viewLifecycleOwner, Observer {
            it.let { dataList ->
//                adapter.data = it
                adapter.submitList(dataList)
            }
        }
        )

        // Add the viewModel to data binding by setting the current UI-Controller as the lifecycle owner
        // Then assigning the "sleepTrackerViewModel binding variable" to our sleepTrackerViewModel
        binding.lifecycleOwner = this
        binding.sleepTrackerViewModel = viewModel

        val layoutManager = GridLayoutManager(activity, 3)
        binding.sleepList.layoutManager = layoutManager

        return binding.root
    }
}
