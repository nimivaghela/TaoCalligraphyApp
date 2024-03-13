package com.app.taocalligraphy.ui.program.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemArcviewLeftListBinding
import com.app.taocalligraphy.databinding.ItemArcviewRightListBinding
import com.app.taocalligraphy.models.response.program.UserProgressDetailApiResponse
import com.app.taocalligraphy.utils.extensions.getDateFromYYYYMMDD
import com.app.taocalligraphy.utils.extensions.getMonthDayFromDate
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.mikhaellopez.circularprogressbar.CircularProgressBar

class ProgramProgressAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ARC_LEFT = 0
    val ARC_RIGHT = 1
    private var progressList = ArrayList<UserProgressDetailApiResponse.Data>()

    @SuppressLint("NotifyDataSetChanged")
    fun setProgressData(progressList: ArrayList<UserProgressDetailApiResponse.Data>) {
        this.progressList = progressList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ARC_LEFT) {
            val itemBindingUtil =
                ItemArcviewLeftListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ArcViewLeftViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemArcviewRightListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ArcViewRightViewHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            ARC_LEFT
        } else {
            ARC_RIGHT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val progress = progressList[holder.bindingAdapterPosition]
        if (holder is ArcViewLeftViewHolder) {
            if (holder.bindingAdapterPosition == 0) {
                holder.item.arcViewLeftTop.gone()
            } else {
                holder.item.arcViewLeftTop.visible()
            }

            if (holder.bindingAdapterPosition == (progressList.size - 1)) {
                holder.item.arcViewLeftBottom.gone()
            } else {
                holder.item.arcViewLeftBottom.visible()
            }

            setHolderData(
                holder.itemView.context,
                progress,
                holder.item.tvDay,
                holder.item.tvUserMeditation,
                holder.item.tvTotalMeditation,
                holder.item.tvMinutes,
                holder.item.circleProgressProgram,
                holder.item.tvMonthDay,
                holder.item.dateView
            )

        } else if (holder is ArcViewRightViewHolder) {
            if (holder.bindingAdapterPosition == 0) {
                holder.item.arcViewRightTop.gone()
            } else {
                holder.item.arcViewRightTop.visible()
            }

            if (holder.bindingAdapterPosition == (progressList.size - 1)) {
                holder.item.arcViewRightBottom.gone()
            } else {
                holder.item.arcViewRightBottom.visible()
            }

            setHolderData(
                holder.itemView.context,
                progress,
                holder.item.tvDay,
                holder.item.tvUserMeditation,
                holder.item.tvTotalMeditation,
                holder.item.tvMinutes,
                holder.item.circleProgressProgram,
                holder.item.tvMonthDay,
                holder.item.dateView
            )
        }
    }

    private fun setHolderData(
        context: Context,
        progress: UserProgressDetailApiResponse.Data,
        tvDay: AppCompatTextView,
        tvUserMeditation: AppCompatTextView,
        tvTotalMeditation: AppCompatTextView,
        tvMinutes: AppCompatTextView,
        circleProgressProgram: CircularProgressBar,
        tvMonthDay: AppCompatTextView,
        viewCircle: View
    ) {
        tvDay.text = String.format(
            "%02d", progress.dayNo ?: 0
        )
        tvUserMeditation.text = progress.userMeditations.toString()
        tvTotalMeditation.text = progress.totalMeditations.toString()
        tvMinutes.text = progress.minutes.toString()
        val completedPercentage =
            ((progress.userMeditations ?: 0) * 100) / (progress.totalMeditations ?: 1).toFloat()
        circleProgressProgram.progress = completedPercentage
        viewCircle.setBackgroundResource(R.drawable.bg_shimmer_light_round)
        tvDay.setTextColor(
            ContextCompat.getColor(
                context,
                R.color.medium_grey
            )
        )
        if (!progress.date.isNullOrEmpty()) {
            val convertToDate = getDateFromYYYYMMDD(progress.date)
            if (DateUtils.isToday(convertToDate.time)) {
                tvMonthDay.text = context.getString(R.string.today)
                viewCircle.setBackgroundResource(R.drawable.bg_medium_gray_round)
                tvDay.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            } else if (DateUtils.isToday(convertToDate.time + DateUtils.DAY_IN_MILLIS)) {
                tvMonthDay.text = context.getString(R.string.yesterday)
            } else {
                tvMonthDay.text = getMonthDayFromDate(progress.date)
            }
        } else {
            tvMonthDay.text = ""
        }
    }

    override fun getItemCount(): Int {
        return progressList.size
    }

    class ArcViewLeftViewHolder(view: ItemArcviewLeftListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ArcViewRightViewHolder(view: ItemArcviewRightListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}