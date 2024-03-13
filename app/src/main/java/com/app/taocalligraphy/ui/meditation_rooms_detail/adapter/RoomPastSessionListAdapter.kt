package com.app.taocalligraphy.ui.meditation_rooms_detail.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemRoomPastSessionListBinding

class RoomPastSessionListAdapter() : RecyclerView.Adapter<RoomPastSessionListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemRoomPastSessionListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0) {
            holder.item.llMain.setBackgroundColor(ContextCompat.getColor(holder.item.llMain.context, R.color.white))
        } else {
            holder.item.llMain.setBackgroundColor(ContextCompat.getColor(holder.item.llMain.context, R.color.medium_grey_10))
        }
    }

    override fun getItemCount(): Int {
        return 2
    }

    class ViewHolder(view: ItemRoomPastSessionListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}