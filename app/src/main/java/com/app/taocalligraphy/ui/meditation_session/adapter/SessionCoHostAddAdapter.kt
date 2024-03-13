package com.app.taocalligraphy.ui.meditation_session.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemBannerListBinding
import com.app.taocalligraphy.databinding.ItemCoHostAddListBinding
import com.app.taocalligraphy.databinding.ItemWeeklyRepeatListBinding


class SessionCoHostAddAdapter() :
    RecyclerView.Adapter<SessionCoHostAddAdapter.ViewHolder>() {

    var size = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemCoHostAddListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.ivRemoveCoHost.setOnClickListener {
            size--
            notifyItemRemoved(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return size
    }

    class ViewHolder(view: ItemCoHostAddListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}