package com.app.taocalligraphy.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemChatFriendListBinding

class ChatFriendAdapter() :
    RecyclerView.Adapter<ChatFriendAdapter.ViewHolder>() {

    val itemList = intArrayOf(
        R.drawable.dummy_master_allan,
        R.drawable.dummy_master_cecilia,
        R.drawable.dummy_master_david,
        R.drawable.dummy_master_francisco,
        R.drawable.ic_dummy_host_image,
        R.drawable.ic_profile_dummy2,
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemChatFriendListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.ivFriend.setImageResource(itemList[holder.adapterPosition])
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(view: ItemChatFriendListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}