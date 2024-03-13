package com.app.taocalligraphy.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemChatMultiFriendListBinding

class ChatMultiFriendConversationAdapter :
    RecyclerView.Adapter<ChatMultiFriendConversationAdapter.ViewHolder>() {

    var itemList = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemChatMultiFriendListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }

    override fun getItemCount(): Int {
        return itemList
    }

    class ViewHolder(view: ItemChatMultiFriendListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}