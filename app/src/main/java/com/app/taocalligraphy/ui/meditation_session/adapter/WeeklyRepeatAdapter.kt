package com.app.taocalligraphy.ui.meditation_session.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemBannerListBinding
import com.app.taocalligraphy.databinding.ItemWeeklyRepeatListBinding


class WeeklyRepeatAdapter(private var context: Context) :
    RecyclerView.Adapter<WeeklyRepeatAdapter.ViewHolder>() {

    var imageList = arrayOf(
        context.getString(R.string.sunday),
        context.getString(R.string.monday),
        context.getString(R.string.tuesday),
        context.getString(R.string.wednesday),
        context.getString(R.string.thursday),
        context.getString(R.string.friday),
        context.getString(R.string.saturday),
    )

    var selectedList = booleanArrayOf(
        false,
        false,
        false,
        false,
        false,
        false,
        false,
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemWeeklyRepeatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.tvDayName.text = imageList[holder.adapterPosition]
        if (selectedList[holder.adapterPosition]) {
            holder.item.llMain.setBackgroundResource(R.drawable.bg_gold_round)
            holder.item.tvDayName.setTextColor(
                ContextCompat.getColor(
                    holder.item.tvDayName.context,
                    R.color.white
                )
            )
        } else {
            holder.item.llMain.setBackgroundResource(R.drawable.bg_white_light_grey_circle)
            holder.item.tvDayName.setTextColor(
                ContextCompat.getColor(
                    holder.item.tvDayName.context,
                    R.color.gold
                )
            )
        }

        holder.itemView.setOnClickListener {
            selectedList[holder.adapterPosition] = !selectedList[holder.adapterPosition]
            notifyItemChanged(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class ViewHolder(view: ItemWeeklyRepeatListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}