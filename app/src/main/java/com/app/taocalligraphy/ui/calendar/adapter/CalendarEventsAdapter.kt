package com.app.taocalligraphy.ui.calendar.adapter

import android.content.Context
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemCalenderEventListBinding

class CalendarEventsAdapter(
    private var context: Context
) :
    RecyclerView.Adapter<CalendarEventsAdapter.ViewHolder>() {



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {


        val itemBindingUtil = ItemCalenderEventListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.viewShimmerBg.visibility = View.GONE
        when {
            holder.adapterPosition == 0 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.transparent
                    )
                )
                holder.item.ivMainPhoto.setImageResource(R.drawable.vd_challenges_icon_for_calendar)
                holder.item.tvHostLiveLabel.visibility = View.GONE
                holder.item.ivHostBorder.visibility = View.GONE
                holder.item.ivSessionMedia.visibility = View.GONE
                holder.item.tvTime.text = ""
                holder.item.tvTime.visibility = View.GONE
                holder.item.tvCompleted.visibility = View.VISIBLE
                holder.item.tvTitle.text = context.getString(R.string.don_one_act_unconditional_kindness)
                holder.item.tvSubTitle.text = context.getString(R.string.day_2_of_kindness_challenge)
                holder.item.ivSessionByTaoSymbol.visibility = View.GONE
                holder.item.tvYouAreHosting.visibility = View.GONE
            }
            position == 1 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.gray_95
                    )
                )
                holder.item.ivMainPhoto.setImageResource(R.drawable.ic_dummy_host_image)
                holder.item.tvHostLiveLabel.visibility = View.VISIBLE
                holder.item.ivHostBorder.visibility = View.GONE
                holder.item.ivSessionMedia.visibility = View.GONE
                holder.item.tvTime.text = context.getString(R.string.threetofour)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvCompleted.visibility = View.GONE
                holder.item.tvTitle.text = context.getString(R.string.breathwork_for_boosting_energy)
                holder.item.tvSubTitle.text = context.getString(R.string.with_name)
                holder.item.ivSessionByTaoSymbol.visibility = View.GONE
                holder.item.tvYouAreHosting.visibility = View.GONE
            }
            position == 2 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.transparent
                    )
                )
                holder.item.ivMainPhoto.setImageResource(R.drawable.ic_profile_dummy2)
                holder.item.tvHostLiveLabel.visibility = View.GONE
                holder.item.ivHostBorder.visibility = View.VISIBLE
                holder.item.ivSessionMedia.visibility = View.GONE
                holder.item.tvTime.text = context.getString(R.string.five_thirty_to_six)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvCompleted.visibility = View.GONE
                holder.item.tvTitle.text = context.getString(R.string.wellness_boost)
                holder.item.tvSubTitle.text = context.getString(R.string.with_francisco)
                holder.item.ivSessionByTaoSymbol.visibility = View.VISIBLE
                holder.item.tvYouAreHosting.text =
                    holder.item.tvYouAreHosting.context.getString(R.string.you_are_hosting)
                holder.item.tvYouAreHosting.visibility = View.VISIBLE
            }
            position == 3 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.gray_95
                    )
                )
                holder.item.ivMainPhoto.setImageResource(R.drawable.ic_profile_dummy2)
                holder.item.tvHostLiveLabel.visibility = View.GONE
                holder.item.ivHostBorder.visibility = View.GONE
                holder.item.ivSessionMedia.visibility = View.GONE
                holder.item.tvTime.text = context.getString(R.string.six_to_seven)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvCompleted.visibility = View.GONE
                holder.item.tvTitle.text = context.getString(R.string.self_love_practice_hour)
                holder.item.tvSubTitle.text = context.getString(R.string.with_elizabeth)
                holder.item.ivSessionByTaoSymbol.visibility = View.GONE
                holder.item.tvYouAreHosting.text =
                    holder.item.tvYouAreHosting.context.getString(R.string.you_are_co_hosting_with) + context.getString(
                                            R.string.elizabeth)
                holder.item.tvYouAreHosting.visibility = View.VISIBLE
            }
            position == 4 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.transparent
                    )
                )
                holder.item.ivMainPhoto.setImageResource(R.drawable.ic_nature_background_dummy)
                holder.item.tvHostLiveLabel.visibility = View.GONE
                holder.item.ivHostBorder.visibility = View.GONE
                holder.item.ivSessionMedia.visibility = View.VISIBLE
                holder.item.tvTime.text = context.getString(R.string.eleven_pm)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvCompleted.visibility = View.GONE
                holder.item.tvTitle.text = context.getString(R.string.sount_sleep)
                holder.item.tvSubTitle.text = context.getString(R.string.day_two_of_7_days_to_better_sleep)
                holder.item.ivSessionByTaoSymbol.visibility = View.GONE
                holder.item.tvYouAreHosting.visibility = View.GONE

                holder.item.viewShimmerBg.isVisible = true
                val paint = Paint()
                paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.MULTIPLY)
                holder.item.viewShimmerBg.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
            }
            else -> {
                // noting to do
            }
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    class ViewHolder(view: ItemCalenderEventListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}