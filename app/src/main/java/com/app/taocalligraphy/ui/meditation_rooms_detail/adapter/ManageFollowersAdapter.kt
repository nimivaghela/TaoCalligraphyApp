package com.app.taocalligraphy.ui.meditation_rooms_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemParticipantsListBinding

class ManageFollowersAdapter(
    val context: Context,
    val mListener: OnFollowersClickListener
) : RecyclerView.Adapter<ManageFollowersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemParticipantsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        /*if ((holder.adapterPosition == 0) or (holder.adapterPosition == 1)) {
            holder.item.ivHostSymbol.isVisible = true
        } else {
            holder.item.ivHostSymbol.isGone = true
        }

        if (holder.adapterPosition == 1) {
            holder.item.ivVoiceLeft.isVisible = true
            holder.item.ivVoiceRight.isVisible = true
        } else {
            holder.item.ivVoiceLeft.isInvisible = true
            holder.item.ivVoiceRight.isInvisible = true
        }*/

        holder.item.ivHostSymbol.visibility = View.GONE
        holder.item.ivVoiceLeft.visibility = View.GONE
        holder.item.ivVoiceRight.visibility = View.GONE
        holder.item.tvName.visibility = View.GONE
        holder.item.tvNameStart.visibility = View.VISIBLE

        holder.item.ivCoHost.setImageResource(R.drawable.ic_profile_dummy2)
        holder.item.tvNameStart.text = context.getString(R.string.fname_lname)

        holder.itemView.setOnClickListener {
            mListener.onFollowersClick(holder.item.viewCenter)
        }
    }

    override fun getItemCount(): Int {
        return 12
    }

    class ViewHolder(view: ItemParticipantsListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnFollowersClickListener {
        fun onFollowersClick(itemView: View)
    }
}