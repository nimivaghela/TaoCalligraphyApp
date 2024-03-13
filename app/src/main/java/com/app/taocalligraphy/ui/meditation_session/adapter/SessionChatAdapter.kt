package com.app.taocalligraphy.ui.meditation_session.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.*
import com.app.taocalligraphy.models.IconNamePopupModel
import com.skydoves.powermenu.CustomPowerMenu


class SessionChatAdapter(val chatMessageListener: ChatMessageListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val itemBindingUtil =
                ItemSessionChatSenderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return SenderViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemSessionChatReceiverBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return ReceiverViewHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 4 == 1) {
            1
        } else {
            0
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SenderViewHolder) {
            holder.itemView.setOnClickListener {
                if (holder.item.rlContent.elevation == 0f) {
                    if (customPowerMenu==null){
                        holder.item.rlContent.setBackgroundResource(R.drawable.bg_dark_gray_gradient)
                        holder.item.rlContent.elevation = 25f
                        chatMessageListener.onChatMessageClick(holder.item.viewDropDown)
                    }else{
                        customPowerMenu?.dismiss()
                        customPowerMenu=null
                    }
                } else {
                    holder.item.rlContent.setBackgroundColor(Color.TRANSPARENT)
                    holder.item.rlContent.elevation = 0f
                    customPowerMenu?.dismiss()
                    customPowerMenu=null
                }
            }
        }else if (holder is ReceiverViewHolder) {
            holder.itemView.setOnClickListener {
                if (holder.item.rlContent.elevation == 0f) {
                    if (customPowerMenu==null){
                        holder.item.rlContent.elevation = 25f
                        chatMessageListener.onChatMessageClick(holder.item.viewDropDown)
                    }else{
                        customPowerMenu?.dismiss()
                        customPowerMenu=null
                    }
                } else {
                    holder.item.rlContent.elevation = 0f
                    customPowerMenu?.dismiss()
                    customPowerMenu=null
                }
            }
        }
    }


    override fun getItemCount(): Int {
        return 10
    }

    class SenderViewHolder(view: ItemSessionChatSenderBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ReceiverViewHolder(view: ItemSessionChatReceiverBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ChatMessageListener{
        fun onChatMessageClick(itemView: View)
    }
}