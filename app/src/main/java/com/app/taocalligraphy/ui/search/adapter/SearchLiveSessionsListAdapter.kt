package com.app.taocalligraphy.ui.search.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLiveSessionsSearchBinding
import com.app.taocalligraphy.ui.meditation_rooms_detail.adapter.UpcomingSessionsListAdapter

class SearchLiveSessionsListAdapter(
    private val signupOptionListener: SignupOptionListener
) :
    RecyclerView.Adapter<SearchLiveSessionsListAdapter.ViewHolder>() {

    private var isCollapsed = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemLiveSessionsSearchBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.apply {
            cvGetRooms.setOnClickListener {
                /*if (isCollapsed) {
                    holder.item.ivArrow.setImageResource(R.drawable.vd_up_arrow_gold)
                    val layoutParams = holder.item.cvGetRooms.layoutParams
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                    holder.item.cvGetRooms.layoutParams = layoutParams
                    isCollapsed = false
                } else {
                    holder.item.ivArrow.setImageResource(R.drawable.vd_down_arrow_gold)
                    val layoutParams = holder.item.cvGetRooms.layoutParams
                    layoutParams.height = 78
                    holder.item.cvGetRooms.layoutParams = layoutParams
                    isCollapsed = true
                }*/
                signupOptionListener.onSignUpClick()
            }

            ivCalendar.setOnClickListener {

            }

            ivFavourite.setOnClickListener {

            }
        }

        when (position) {
            0 -> {
                holder.item.llCalendarFavourite.visibility = View.VISIBLE
                holder.item.ivCalendar.setImageResource(R.drawable.vd_add_to_calendar_gold)
                holder.item.cvGetRooms.visibility = View.GONE
            }
            1 -> {
                holder.item.llCalendarFavourite.visibility = View.GONE
                holder.item.cvGetRooms.visibility = View.VISIBLE
            }
            2 -> {
                holder.item.llCalendarFavourite.visibility = View.VISIBLE
                holder.item.ivCalendar.setImageResource(R.drawable.vd_add_to_calendar_grey)
                holder.item.cvGetRooms.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ViewHolder(view: ItemLiveSessionsSearchBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface SignupOptionListener {
        fun onSignUpClick()
    }
}