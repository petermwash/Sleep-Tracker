package com.pemwa.sleeptracker.sleeptracker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.pemwa.sleeptracker.R
import com.pemwa.sleeptracker.database.SleepNight
import com.pemwa.sleeptracker.databinding.ListItemSleepNightBinding
import com.pemwa.sleeptracker.util.convertDurationToFormatted
import com.pemwa.sleeptracker.util.convertNumericQualityToString

class SleepNightAdapter(private val clickListener: SleepNightListener): ListAdapter<SleepNight, SleepNightAdapter.CustomViewHolder>(SleepNightDiffCallback()) {
//    var data = listOf<SleepNight>()
//        set(value) {
//            field = value
//            notifyDataSetChanged()
//        }
//
//    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val item = data[position]
        val item = getItem(position)
        holder.bind(clickListener,item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder.from(parent)
    }

    class CustomViewHolder private constructor(private val binding: ListItemSleepNightBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: SleepNightListener, item: SleepNight) {
            binding.sleep = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): CustomViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemSleepNightBinding
                    .inflate(layoutInflater, parent, false)

                return CustomViewHolder(binding)
            }
        }

    }
}

class SleepNightDiffCallback : DiffUtil.ItemCallback<SleepNight>() {
    override fun areItemsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem.nightId == newItem.nightId
    }

    override fun areContentsTheSame(oldItem: SleepNight, newItem: SleepNight): Boolean {
        return oldItem == newItem
    }
}

class SleepNightListener(val clickListener: (sleepId: Long) -> Unit) {
    fun onClick(night: SleepNight) = clickListener(night.nightId)
}
