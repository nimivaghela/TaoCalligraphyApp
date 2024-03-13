package com.app.taocalligraphy.ui.chat.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.*


class ChatFriendSelectionAdapter(val friendSelectionListener: FriendSelectionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SECTION = 0
    private val ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM) {
            val itemBindingUtil =
                ItemChatFriendSelectionListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return FriendViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemChatFriendCommunityTitleListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            return TitleHolder(itemBindingUtil)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TitleHolder) {
            holder.item.llMain.setBackgroundColor(
                getItemBackground(
                    holder.itemView.context,
                    holder.adapterPosition
                )
            )
            holder.item.tvSectionTitle.text =
                holder.itemView.context.getString(R.string.friends)
            holder.item.tvSectionTitle.typeface =
                ResourcesCompat.getFont(holder.itemView.context, R.font.font_lato_bold)
            holder.item.tvSectionTitle.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.dark_grey
                )
            )
        } else if (holder is FriendViewHolder) {
            holder.item.llMain.setBackgroundColor(
                getItemBackground(
                    holder.itemView.context,
                    holder.adapterPosition
                )
            )
            val content = SpannableString("Eleanor Pena")
            content.setSpan(UnderlineSpan(), 0, 1, 0)
            //holder.item.tvName.text = content

            holder.itemView.setOnClickListener {
                friendSelectionListener.onFriendItemClick()
            }
        }
    }

    private fun getItemBackground(context: Context, position: Int): Int {
        return if (position % 2 == 0) {
            ContextCompat.getColor(
                context,
                R.color.white_10
            )
        } else {
            ContextCompat.getColor(
                context,
                R.color.gold_10
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> {
                SECTION
            }
            else -> {
                ITEM
            }
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    class FriendViewHolder(view: ItemChatFriendSelectionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class TitleHolder(view: ItemChatFriendCommunityTitleListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface FriendSelectionListener {
        fun onFriendItemClick()
        fun onCommunityMoreClick(itemView: View, adapterPosition: Int)
    }
}