package com.app.taocalligraphy.ui.profile_rewards.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemRewardHistoryBinding

class ProfileRewardAdapter : RecyclerView.Adapter<ProfileRewardAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemRewardHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.apply {
        }
    }

    override fun getItemCount(): Int {
        return 20
    }

    inner class ViewHolder(view: ItemRewardHistoryBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view


    }
}