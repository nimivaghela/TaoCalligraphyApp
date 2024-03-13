package com.app.taocalligraphy.ui.challenges.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemChallengeDateDayBinding

class ChallengeDaySelectSingleAdapter(
    private val context: Context,
    private val mListener: OnDayClickListener,
) : RecyclerView.Adapter<ChallengeDaySelectSingleAdapter.ItemViewHolder>() {
    var selectedPosition = -1
    var isDateShow = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_challenge_date_day,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 7


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            if (selectedPosition == position) {
                ivBorder.borderColor = ContextCompat.getColor(ivBorder.context, R.color.gold)
                tvDayNumber.setTextColor(
                    ContextCompat.getColor(
                        tvDayNumber.context,
                        R.color.gold
                    )
                )
            } else {
                ivBorder.borderColor =
                    ContextCompat.getColor(ivBorder.context, R.color.shimmer_light)
                tvDayNumber.setTextColor(
                    ContextCompat.getColor(
                        tvDayNumber.context,
                        R.color.medium_grey
                    )
                )
            }
            if (isDateShow) {
                tvDate.visibility = View.VISIBLE
            } else {
                tvDate.visibility = View.GONE
            }
            when (position) {
                0 -> {
                    tvDayNumber.text = context.getString(R.string.one)
                    tvDate.text = context.getString(R.string.NOV_30)
                }
                1 -> {
                    tvDayNumber.text = context.getString(R.string.two)
                    tvDate.text = context.getString(R.string.DEC_01)
                }
                2 -> {
                    tvDayNumber.text = context.getString(R.string.three)
                    tvDate.text = context.getString(R.string.DEC_02)
                }
                3 -> {
                    tvDayNumber.text = context.getString(R.string.four)
                    tvDate.text = context.getString(R.string.DEC_03)
                }
                4 -> {
                    tvDayNumber.text = context.getString(R.string.five)
                    tvDate.text = context.getString(R.string.DEC_04)
                }
                5 -> {
                    tvDayNumber.text = context.getString(R.string.six)
                    tvDate.text = context.getString(R.string.DEC_05)
                }
                6 -> {
                    tvDayNumber.text = context.getString(R.string.seven)
                    tvDate.text = context.getString(R.string.DEC_06)
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemChallengeDateDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.fmDay.setOnClickListener {
                mListener.onDayClick(bindingAdapterPosition)
                selectedPosition = bindingAdapterPosition
                notifyDataSetChanged()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun onUpdateDateUI() {
        isDateShow = true
        notifyDataSetChanged()
    }

    interface OnDayClickListener {
        fun onDayClick(position: Int)
    }
}