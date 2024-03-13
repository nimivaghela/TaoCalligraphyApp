package com.app.taocalligraphy.ui.wellness.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemWeekDateAndDayBinding
import com.app.taocalligraphy.models.WeekDatesDayListModel

class WeekCalendarAdapter(
    private var mDataList: MutableList<WeekDatesDayListModel>,
    private val mListener: OnWeekAdapterItemClickListener
) : RecyclerView.Adapter<WeekCalendarAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_week_date_and_day,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvDays.text = days
                tvDate.text = dates
                if (isEvent) {
                    tvIndicator.visibility = View.VISIBLE
                } else {
                    tvIndicator.visibility = View.GONE
                }
                if (position == 0) {
                    tvDays.setTextColor(
                        ContextCompat.getColor(
                            tvDays.context,
                            R.color.gold_semi_light
                        )
                    )
                    tvDate.setTextColor(ContextCompat.getColor(tvDays.context, R.color.white))
                    tvDate.setBackgroundResource(R.drawable.bg_gold_semi_light_round)
                    tvIndicator.setBackgroundResource(R.drawable.bg_gold_semi_light_round)
                } else {
                    tvDays.setTextColor(
                        ContextCompat.getColor(
                            tvDays.context,
                            R.color.dark_grey
                        )
                    )
                    tvDate.setTextColor(
                        ContextCompat.getColor(
                            tvDays.context,
                            R.color.dark_grey
                        )
                    )
                    tvDate.setBackgroundResource(R.drawable.bg_white_light_grey)
                    tvIndicator.setBackgroundResource(R.drawable.bg_light_gery_round)
                }
            }
        }
    }

    inner class ItemViewHolder(var binding: ItemWeekDateAndDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                mListener.onWeekAdapterClick(mDataList[bindingAdapterPosition])
            }
        }
    }

    interface OnWeekAdapterItemClickListener {
        fun onWeekAdapterClick(weekDatesDayListModel: WeekDatesDayListModel)
    }
}