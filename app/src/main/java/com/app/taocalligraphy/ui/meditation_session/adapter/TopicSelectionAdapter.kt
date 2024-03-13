package com.app.taocalligraphy.ui.meditation_session.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemTopicSelectListBinding

class TopicSelectionAdapter() :
    RecyclerView.Adapter<TopicSelectionAdapter.ViewHolder>() {

    var currentSelectionPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemTopicSelectListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (currentSelectionPosition == holder.adapterPosition) {
            holder.item.ivTopicSelect.isVisible = true
            holder.item.ivTopic.setImageResource(R.drawable.vd_productivity_white)
        } else {
            holder.item.ivTopicSelect.isGone = true
            holder.item.ivTopic.setImageResource(R.drawable.vd_productivity_black)
        }

        holder.itemView.setOnClickListener {
            currentSelectionPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }

    class ViewHolder(view: ItemTopicSelectListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}