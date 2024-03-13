package com.app.taocalligraphy.ui.meditation_session.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemParticipantsListBinding

class ManageParticipantsAdapter(val participantsClickListener: ParticipantsClickListener) :
    RecyclerView.Adapter<ManageParticipantsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemParticipantsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if ((holder.adapterPosition==0) or (holder.adapterPosition==1)){
            holder.item.ivHostSymbol.isVisible=true
        }else{
            holder.item.ivHostSymbol.isGone=true
        }

        if (holder.adapterPosition==1){
            holder.item.ivVoiceLeft.isVisible=true
            holder.item.ivVoiceRight.isVisible=true
        }else{
            holder.item.ivVoiceLeft.isInvisible=true
            holder.item.ivVoiceRight.isInvisible=true
        }

        holder.itemView.setOnClickListener {
            participantsClickListener.onParticipantsClick(holder.item.viewCenter)
        }
    }

    override fun getItemCount(): Int {
        return 18
    }

    class ViewHolder(view: ItemParticipantsListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ParticipantsClickListener{
        fun onParticipantsClick(itemView: View)
    }
}