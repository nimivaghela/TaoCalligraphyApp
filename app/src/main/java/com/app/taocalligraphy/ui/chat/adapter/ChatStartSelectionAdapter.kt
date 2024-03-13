package com.app.taocalligraphy.ui.chat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemStartChatListBinding

class ChatStartSelectionAdapter(
    val chatRemoveFriendListener: ChatRemoveFriendListener,
    val removeImageResource: Int
) :
    RecyclerView.Adapter<ChatStartSelectionAdapter.ViewHolder>() {

    var item = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemStartChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.ivRemoveFromChat.setImageResource(removeImageResource)
        holder.item.ivRemoveFromChat.setOnClickListener {
            chatRemoveFriendListener.onFriendRemoveFromChat(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return item
    }

    class ViewHolder(view: ItemStartChatListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ChatRemoveFriendListener {
        fun onFriendRemoveFromChat(adapterPosition: Int)
    }
}