package com.app.taocalligraphy.ui.program.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemProgramsDateDayBinding
import com.app.taocalligraphy.models.response.program.UserProgramApiResponse
import com.app.taocalligraphy.utils.extensions.*
import java.util.*

class ProgramsDaySelectSingleAdapter(
    private val mListener: OnDayClickListener,
    selectedPos: Int
) : RecyclerView.Adapter<ProgramsDaySelectSingleAdapter.ItemViewHolder>() {

    var selectedPosition = selectedPos
    var isDateShow = false
    private var mDataList: ArrayList<UserProgramApiResponse.Days> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_programs_date_day, parent, false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            if ((mDataList[position].programDay ?: 0) <= 9) {
                tvDayNumber.text = "0${mDataList[position].programDay}"
            } else {
                tvDayNumber.text = mDataList[position].programDay.toString()
            }

            if (!mDataList[position].programDate.isNullOrEmpty())
                tvDate.text = "" + getMonthDayFromDate(mDataList[position].programDate)

            if (mDataList[position].programDate == dateFormatter_yyyy_mm_dd.format(
                    Calendar.getInstance().time
                )
            ) {
                fmDay.alpha = 1f
                if (selectedPosition != -1) {
                    if (position == selectedPosition) {
                        ivBackground.visible()
                        ivBorder.gone()
                        ivBackground.setColorFilter(
                            ContextCompat.getColor(
                                ivBorder.context,
                                R.color.gold
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                        tvDayNumber.setTextColor(
                            ContextCompat.getColor(
                                tvDayNumber.context,
                                R.color.white
                            )
                        )
                    } else {
                        ivBackground.visible()
                        ivBorder.gone()
                        ivBackground.setColorFilter(
                            ContextCompat.getColor(
                                ivBorder.context,
                                R.color.medium_grey
                            ), android.graphics.PorterDuff.Mode.SRC_IN
                        )
                        tvDayNumber.setTextColor(
                            ContextCompat.getColor(
                                tvDayNumber.context,
                                R.color.white
                            )
                        )
                    }
                } else {
                    ivBackground.visible()
                    ivBorder.gone()
                    ivBackground.setColorFilter(
                        ContextCompat.getColor(
                            ivBorder.context,
                            R.color.gold
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(
                            tvDayNumber.context,
                            R.color.white
                        )
                    )
                    selectedPosition = position
                    mListener.onDaySelected(selectedPosition)
                }
            } else if (getCurrentDate().after(
                    dateFormatter_yyyy_mm_dd.parse(
                        mDataList[position].programDate ?: dateFormatter_yyyy_mm_dd.format(
                            Calendar.getInstance().time
                        )
                    )
                )
            ) {
                fmDay.alpha = 0.5f
                if (position == selectedPosition) {
                    ivBackground.visible()
                    ivBorder.visible()
                    ivBackground.setColorFilter(
                        ContextCompat.getColor(
                            ivBorder.context,
                            R.color.shimmer_light
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(
                            tvDayNumber.context,
                            R.color.medium_grey
                        )
                    )
                    ivBorder.borderColor =
                        ContextCompat.getColor(ivBorder.context, R.color.gold)
                } else {
                    ivBackground.visible()
                    ivBorder.gone()
                    ivBackground.setColorFilter(
                        ContextCompat.getColor(
                            ivBorder.context,
                            R.color.shimmer_light
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(
                            tvDayNumber.context,
                            R.color.medium_grey
                        )
                    )
                }
            } else {
                fmDay.alpha = 1f
                if (position == selectedPosition) {
                    ivBackground.visible()
                    ivBorder.visible()
                    ivBackground.setColorFilter(
                        ContextCompat.getColor(
                            ivBorder.context,
                            R.color.shimmer_light
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(
                            tvDayNumber.context,
                            R.color.gold
                        )
                    )
                    ivBorder.borderColor =
                        ContextCompat.getColor(ivBorder.context, R.color.gold)
                } else {
                    ivBackground.visible()
                    ivBorder.gone()
                    ivBackground.setColorFilter(
                        ContextCompat.getColor(
                            ivBorder.context,
                            R.color.shimmer_light
                        ), android.graphics.PorterDuff.Mode.SRC_IN
                    )
                    tvDayNumber.setTextColor(
                        ContextCompat.getColor(
                            tvDayNumber.context,
                            R.color.medium_grey
                        )
                    )
                }
            }

            if (isDateShow) {
                tvDate.visibility = View.VISIBLE
            } else {
                tvDate.visibility = View.GONE
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemProgramsDateDayBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.fmDay.setOnClickListener {
                if (selectedPosition != bindingAdapterPosition) {
                    mListener.onDayClick(mDataList[bindingAdapterPosition], bindingAdapterPosition)
                    selectedPosition = bindingAdapterPosition
                    mListener.onDaySelected(selectedPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(
        mDataList: ArrayList<UserProgramApiResponse.Days>, isDateShow: Boolean,
        selectedPos: Int
    ) {
        this.mDataList = mDataList
        this.isDateShow = isDateShow
        this.selectedPosition = selectedPos
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSelectedDay(selectedPos: Int) {
        this.selectedPosition = selectedPos
        notifyDataSetChanged()
    }

    interface OnDayClickListener {
        fun onDayClick(position: UserProgramApiResponse.Days, selectedPos: Int)
        fun onDaySelected(position: Int)
    }
}