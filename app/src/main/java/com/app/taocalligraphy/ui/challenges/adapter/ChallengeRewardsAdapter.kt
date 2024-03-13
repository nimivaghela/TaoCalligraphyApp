package com.app.taocalligraphy.ui.challenges.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemChallengeRewardListBinding

class ChallengeRewardsAdapter(
    private var context: Context
) :
    RecyclerView.Adapter<ChallengeRewardsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemChallengeRewardListBinding.inflate(LayoutInflater.from(parent.context),
                parent,
                false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.ivRewardHeart.isGone = true
        when (holder.adapterPosition) {
            0 -> {
                holder.item.tvRewardTitle.text = context.getString(R.string.thirty_silver_heart)
                holder.item.ivRewardHeart.isVisible = true
            }
            1 -> {
                /*holder.item.tvRewardTitle.text =
                    "Entry to a Live Session on \u2028Jan 29, “Gratitude Changes my Life”"*/
                holder.item.tvRewardTitle.text =
                    context.getString(R.string.entry_to_a_live_session)
            }
            else -> {
                holder.item.tvRewardTitle.text =
                    context.getString(R.string.that_warm_feeling_in_the_heart_that_gratitude_brings)
            }
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ViewHolder(view: ItemChallengeRewardListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}