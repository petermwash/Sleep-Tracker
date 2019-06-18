package com.pemwa.sleeptracker.sleeptracker

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pemwa.sleeptracker.R
import com.pemwa.sleeptracker.database.SleepNight
import com.pemwa.sleeptracker.util.convertDurationToFormatted
import com.pemwa.sleeptracker.util.convertNumericQualityToString

class SleepNightAdapter: RecyclerView.Adapter<SleepNightAdapter.CustomViewHolder>() {
    var data = listOf<SleepNight>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder.from(parent)
    }

    class CustomViewHolder private constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sleepLength = itemView.findViewById<TextView>(R.id.sleep_length)
        private val quality = itemView.findViewById<TextView>(R.id.quality_string)
        private val qualityImage = itemView.findViewById<ImageView>(R.id.quality_image)

        fun bind(item: SleepNight) {
            val res = itemView.context.resources
            sleepLength.text = convertDurationToFormatted(item.startTimeMilli, item.endTimeMilli, res)
            quality.text = convertNumericQualityToString(item.sleepQuality, res)
            qualityImage.setImageResource(
                when (item.sleepQuality) {
                    0 -> R.drawable.ic_sleep_0
                    1 -> R.drawable.ic_sleep_1
                    2 -> R.drawable.ic_sleep_2
                    3 -> R.drawable.ic_sleep_3
                    4 -> R.drawable.ic_sleep_4
                    5 -> R.drawable.ic_sleep_5
                    else -> R.drawable.ic_sleep_active
                }
            )
        }

        companion object {
            fun from(parent: ViewGroup): CustomViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val view = layoutInflater
                    .inflate(R.layout.list_item_sleep_night, parent, false)

                return CustomViewHolder(view)
            }
        }

    }
}
