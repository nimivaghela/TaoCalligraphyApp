package com.app.taocalligraphy.ui.wellness.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemCoachesHelpInfoListBinding

class CoachedHelpYouInfoAdapter() :
    RecyclerView.Adapter<CoachedHelpYouInfoAdapter.ViewHolder>() {

    var itemList = intArrayOf(
        R.string.find_purpose,
        R.string.feel_more_connected,
        R.string.overcome_blockages,
        R.string.Become_more_confident,
        R.string.develop_positive_mindsets,
        R.string.create_a_flow,
        R.string.joy_every_day
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemCoachesHelpInfoListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tvInfo.text =
            holder.item.tvInfo.context.getString(itemList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(view: ItemCoachesHelpInfoListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}