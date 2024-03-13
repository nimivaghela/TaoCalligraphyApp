package com.app.taocalligraphy.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemUpcomingSessionListBinding

class UpcomingSessionAdapter(val sessionClickListener: SessionClickListener) :
    RecyclerView.Adapter<UpcomingSessionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemUpcomingSessionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.adapterPosition == 0) {
            holder.item.tvHostLiveLabel.isVisible = true
            holder.item.ivSessionByTaoSymbol.isVisible = true
            holder.item.ivHostBorder.isGone = true
            holder.item.ivSessionMedia.isGone = true
        } else if (position == 1) {
            holder.item.tvHostLiveLabel.isGone = true
            holder.item.ivSessionByTaoSymbol.isInvisible = true
            holder.item.ivHostBorder.isVisible = true
            holder.item.ivSessionMedia.isGone = true
        } else {
            holder.item.tvHostLiveLabel.isGone = true
            holder.item.ivSessionByTaoSymbol.isInvisible = true
            holder.item.ivHostBorder.isGone = true
            holder.item.ivSessionMedia.isVisible = true
        }

        holder.itemView.setOnClickListener {
            sessionClickListener.onSessionClick()
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ViewHolder(view: ItemUpcomingSessionListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface SessionClickListener {
        fun onSessionClick()
    }
}