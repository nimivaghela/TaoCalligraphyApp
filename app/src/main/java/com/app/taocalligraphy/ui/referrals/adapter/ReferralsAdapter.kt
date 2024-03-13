package com.app.taocalligraphy.ui.referrals.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemParticipantsListBinding
import com.app.taocalligraphy.models.response.profile.ReferralsResponse
import com.app.taocalligraphy.utils.extensions.getDateFromYYYYMMDD
import com.app.taocalligraphy.utils.extensions.getFormattedDate
import com.app.taocalligraphy.utils.loadImage

class ReferralsAdapter(
    val mListener: OnFollowersClickListener
) : RecyclerView.Adapter<ReferralsAdapter.ViewHolder>() {

    var list: ArrayList<ReferralsResponse.UserDetails> = ArrayList()

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

        //holder.item.tvDate.visibility = View.VISIBLE

        holder.item.ivHostSymbol.visibility = View.GONE
        holder.item.ivVoiceLeft.visibility = View.GONE
        holder.item.ivVoiceRight.visibility = View.GONE
        holder.item.tvName.visibility = View.GONE
        holder.item.tvDate.visibility = View.VISIBLE
        holder.item.tvNameStart.visibility = View.VISIBLE

        holder.item.apply {
            ivCoHost.loadImage(
                list[position].profilePicUrl,
                R.drawable.ic_profile_default,
                true
            )

//            ivCoHost.setImageResource(R.drawable.ic_profile_dummy2)
            tvNameStart.text = list[position].name
            tvDate.text = getFormattedDate(list[position].joinDate)
        }



        holder.itemView.setOnClickListener {
            mListener.onFollowersClick(holder.item.viewCenter,position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: ItemParticipantsListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnFollowersClickListener {
        fun onFollowersClick(itemView: View, index : Int)
    }

}