package com.app.taocalligraphy.ui.chat.adapter

import android.content.Context
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.*


class ChatFriendCommunitySectionAdapter(val friendSelectionListener: FriendSelectionListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val SECTION = 0
    private val ITEM = 1
    var searchContent = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == ITEM) {
            val itemBindingUtil =
                ItemChatFriendCommunityListBinding.inflate(
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
                    0
                )
            )
            if (holder.adapterPosition == 0) {
                holder.item.tvSectionTitle.text =
                    holder.itemView.context.getString(R.string.friends)
            } else {
                holder.item.tvSectionTitle.text =
                    holder.itemView.context.getString(R.string.community)
            }
        } else if (holder is FriendViewHolder) {
            holder.item.llMain.setBackgroundColor(
                getItemBackground(
                    holder.itemView.context,
                    holder.adapterPosition
                )
            )
            val content = SpannableString("Eleanor Pena")
            if (!searchContent.isNullOrEmpty()) {
                if (content.contains(searchContent, true)) {
                    val startIndex =
                        content.toString().lowercase().indexOf(searchContent.lowercase())
                    val endIndex = startIndex + searchContent.length
                    content.setSpan(UnderlineSpan(), startIndex, endIndex, 0)
                }
            }
            holder.item.tvName.text = content

            if (holder.adapterPosition > 4) {
                holder.item.ivMore.isVisible = true
            } else {
                holder.item.ivMore.isGone = true
            }

            holder.itemView.setOnClickListener {
                friendSelectionListener.onFriendItemClick()
            }

            holder.item.ivMore.setOnClickListener {
                friendSelectionListener.onCommunityMoreClick(
                    holder.item.viewOption,
                    holder.adapterPosition
                )
            }
        }
    }

    private fun getItemBackground(context: Context, position: Int): Int {
        return if (position % 2 == 0) {
            ContextCompat.getColor(
                context,
                R.color.sand_10
            )
        } else {
            ContextCompat.getColor(
                context,
                R.color.white_10
            )
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0, 4 -> {
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

    class FriendViewHolder(view: ItemChatFriendCommunityListBinding) :
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