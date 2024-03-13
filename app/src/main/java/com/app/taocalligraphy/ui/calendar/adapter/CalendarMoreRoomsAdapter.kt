package com.app.taocalligraphy.ui.calendar.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemRoomsListBinding

class CalendarMoreRoomsAdapter(private var context: Context,private val signupOptionListener: SignupOptionListener) :
    RecyclerView.Adapter<CalendarMoreRoomsAdapter.ViewHolder>() {

    private var isCollapsed = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemRoomsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.item.cvGetRooms.setOnClickListener {
            if (isCollapsed) {
                holder.item.ivArrow.setImageResource(R.drawable.vd_up_arrow_gold)
                val layoutParams = holder.item.cvGetRooms.layoutParams
                layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                holder.item.cvGetRooms.layoutParams = layoutParams
                isCollapsed = false
            } else {
                holder.item.ivArrow.setImageResource(R.drawable.vd_down_arrow_gold)
                val layoutParams = holder.item.cvGetRooms.layoutParams
                layoutParams.height = 75
                holder.item.cvGetRooms.layoutParams = layoutParams
                isCollapsed = true
            }
        }

        holder.item.btnSignUp.setOnClickListener {
            signupOptionListener.onSignUpClick()
        }

        when {
            holder.adapterPosition == 0 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.transparent
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
                holder.item.ivAddToCalendarGold.visibility = View.VISIBLE
                holder.item.ivAddToCalendarGrey.visibility = View.GONE
                holder.item.ivAddToFavourite.visibility = View.VISIBLE
                holder.item.cvGetRooms.visibility = View.GONE
                holder.item.cardSignUp.visibility = View.GONE
            }
            position == 1 -> {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.item.rrMain.context,
                        R.color.transparent
                    )
                )
                holder.item.ivMainPhoto.setImageResource(R.drawable.ic_dummy_host_image)
                holder.item.tvHostLiveLabel.visibility = View.GONE
                holder.item.ivHostBorder.visibility = View.VISIBLE
                holder.item.ivSessionMedia.visibility = View.GONE
                holder.item.tvTime.text = context.getString(R.string.five_thirty_to_six)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvCompleted.visibility = View.GONE
                holder.item.tvTitle.text = context.getString(R.string.wellness_boost)
                holder.item.tvSubTitle.text = context.getString(R.string.with_francisco)
                holder.item.ivSessionByTaoSymbol.visibility = View.VISIBLE
                holder.item.ivAddToCalendarGold.visibility = View.GONE
                holder.item.ivAddToCalendarGrey.visibility = View.GONE
                holder.item.ivAddToFavourite.visibility = View.GONE
                holder.item.cvGetRooms.visibility = View.GONE
                holder.item.cardSignUp.visibility = View.VISIBLE
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
                holder.item.ivHostBorder.visibility = View.GONE
                holder.item.ivSessionMedia.visibility = View.GONE
                holder.item.tvTime.text = context.getString(R.string.six_to_seven)
                holder.item.tvTime.visibility = View.VISIBLE
                holder.item.tvCompleted.visibility = View.GONE
                holder.item.tvTitle.text = context.getString(R.string.self_love_practice_hour)
                holder.item.tvSubTitle.text = context.getString(R.string.with_elizabeth)
                holder.item.ivSessionByTaoSymbol.visibility = View.GONE
                holder.item.ivAddToCalendarGold.visibility = View.GONE
                holder.item.ivAddToCalendarGrey.visibility = View.VISIBLE
                holder.item.ivAddToFavourite.visibility = View.VISIBLE
                holder.item.cvGetRooms.visibility = View.GONE
                holder.item.cardSignUp.visibility = View.GONE
            }
            else -> {
                // noting to do
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ViewHolder(view: ItemRoomsListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface SignupOptionListener {
        fun onSignUpClick()
    }
}