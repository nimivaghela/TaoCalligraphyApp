package com.app.taocalligraphy.ui.challenges.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.*

class ChallengeProgressAdapter() :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ARC_LEFT = 0
    val ARC_RIGHT = 1
    var itemSize = 5

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ARC_LEFT) {
            val itemBindingUtil =
                ItemArcviewLeftChallengeListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ArcViewLeftViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemArcviewRightChallengeListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ArcViewRightViewHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) {
            ARC_LEFT
        } else {
            ARC_RIGHT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArcViewLeftViewHolder) {
            if (holder.adapterPosition == 0) {
                holder.item.viewTopHide.isVisible = true
            } else {
                holder.item.viewTopHide.isGone = true
            }

            if (holder.adapterPosition == (itemSize - 1)) {
                holder.item.viewBottomHide.isVisible = true
                holder.item.viewCircle.setBackgroundResource(R.drawable.bg_medium_gray_round)
                holder.item.tvDay.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                holder.item.circleProgressProgram.progress=50f
            } else {
                holder.item.viewBottomHide.isGone = true
                holder.item.viewCircle.setBackgroundResource(R.drawable.bg_shimmer_light_round)
                holder.item.tvDay.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.medium_grey
                    )
                )
                holder.item.circleProgressProgram.progress=100f
            }
        } else if (holder is ArcViewRightViewHolder) {
            if (holder.adapterPosition == 0) {
                holder.item.viewTopHide.isVisible = true
            } else {
                holder.item.viewTopHide.isGone = true
            }

            if (holder.adapterPosition == (itemSize - 1)) {
                holder.item.viewBottomHide.isVisible = true
                holder.item.viewCircle.setBackgroundResource(R.drawable.bg_medium_gray_round)
                holder.item.tvDay.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
                holder.item.circleProgressProgram.progress=50f
            } else {
                holder.item.viewBottomHide.isGone = true
                holder.item.viewCircle.setBackgroundResource(R.drawable.bg_shimmer_light_round)
                holder.item.tvDay.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.medium_grey
                    )
                )
                holder.item.circleProgressProgram.progress=100f
            }
        }
    }

    override fun getItemCount(): Int {
        return itemSize
    }

    class ArcViewLeftViewHolder(view: ItemArcviewLeftChallengeListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ArcViewRightViewHolder(view: ItemArcviewRightChallengeListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}