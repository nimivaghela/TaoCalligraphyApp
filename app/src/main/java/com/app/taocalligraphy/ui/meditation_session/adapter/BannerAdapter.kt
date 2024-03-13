package com.app.taocalligraphy.ui.meditation_session.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemBannerListBinding


class BannerAdapter() :
    RecyclerView.Adapter<BannerAdapter.ViewHolder>() {

    var currentSelectionPosition = -1
    var imageList = intArrayOf(
        R.drawable.ic_dummy_banner_first,
        R.drawable.ic_dummy_banner_second,
        R.drawable.ic_dummy_banner_third,
        R.drawable.ic_dummy_banner_forth
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemBannerListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.ivBanner.setImageResource(imageList[holder.adapterPosition])
        if (currentSelectionPosition == holder.adapterPosition) {
            holder.item.cardBanner.cardElevation = 3.0f
            holder.item.cardBanner.strokeColor =
                ContextCompat.getColor(holder.item.cardBanner.context, R.color.gold)
        } else {
            holder.item.cardBanner.cardElevation = 0.0f
            holder.item.cardBanner.strokeColor =
                ContextCompat.getColor(holder.item.cardBanner.context, android.R.color.transparent)
        }

        holder.itemView.setOnClickListener {
            currentSelectionPosition = holder.adapterPosition
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(view: ItemBannerListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}