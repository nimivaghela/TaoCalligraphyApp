package com.app.taocalligraphy.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemFeatureSessionListBinding

class FeatureSessionAdapter(var context: Context) :
    RecyclerView.Adapter<FeatureSessionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemFeatureSessionListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.adapterPosition % 2 === 0) {
            holder.item.tvLiveSessionLabel.isVisible = true
            holder.item.tvSessionDay.isVisible = true
            holder.item.tvLiveSessionDay.isGone = true
            holder.item.tvSessionDateGold.isGone = true

            holder.item.llDate.background =
                context.getDrawable(R.drawable.bg_gold_white_border_12dp)
            holder.item.tvSessionDay.updatePadding(
                top = holder.item.tvSessionDay.context.resources.getDimension(
                    R.dimen._2sdp
                ).toInt(), bottom = holder.item.tvSessionDay.context.resources.getDimension(
                    R.dimen._3sdp
                ).toInt()
            )
        } else {
            holder.item.llDate.background =
                context.getDrawable(R.drawable.bg_gold_without_border_12dp)
            holder.item.tvLiveSessionDay.isVisible = true
            holder.item.tvSessionDateGold.isVisible = true
            holder.item.tvSessionDay.isGone = true
            holder.item.tvLiveSessionLabel.isGone = true

            holder.item.tvSessionDay.updatePadding(
                top = holder.item.tvSessionDay.context.resources.getDimension(
                    R.dimen._10sdp
                ).toInt(), bottom = holder.item.tvSessionDay.context.resources.getDimension(
                    R.dimen._10sdp
                ).toInt()
            )
        }
    }

    override fun getItemCount(): Int {
        return 4
    }

    class ViewHolder(view: ItemFeatureSessionListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}