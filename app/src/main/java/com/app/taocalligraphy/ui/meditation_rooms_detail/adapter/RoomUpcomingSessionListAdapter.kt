package com.app.taocalligraphy.ui.meditation_rooms_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemRoomUpcomingSessionListBinding

class RoomUpcomingSessionListAdapter(
    private var mListener: OnSessionClick,
    private var onCalendarClicked: OnCalendarClicked
) : RecyclerView.Adapter<RoomUpcomingSessionListAdapter.ViewHolder>() {

    companion object {
        var isAddedToCal: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemRoomUpcomingSessionListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0) {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.item.llMain.context,
                    R.color.white
                )
            )
        } else {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.item.llMain.context,
                    R.color.medium_grey_10
                )
            )
        }

        holder.item.llMain.setOnClickListener {
            mListener.onSessionItemClick(position % 2 == 0)
        }

        holder.item.ivGreyCalender.setOnClickListener {
            holder.item.ivGoldCalender.isVisible = true
            holder.item.ivGreyCalender.isGone = true
        }

        holder.item.ivGoldCalender.setOnClickListener {
            holder.item.ivGoldCalender.isGone = true
            holder.item.ivGreyCalender.isVisible = true
        }

        holder.item.ivGreyFav.setOnClickListener {
            holder.item.ivGoldFav.isVisible = true
            holder.item.ivGreyFav.isGone = true
        }

        holder.item.ivGoldFav.setOnClickListener {
            holder.item.ivGoldFav.isGone = true
            holder.item.ivGreyFav.isVisible = true
        }


    }

    override fun getItemCount(): Int {
        return 2
    }

    class ViewHolder(view: ItemRoomUpcomingSessionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnSessionClick {
        fun onSessionItemClick(isHost: Boolean)
    }

    interface OnCalendarClicked {
        fun onCalendarClicked(position: Int)
    }
}