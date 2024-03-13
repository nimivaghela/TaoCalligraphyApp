package com.app.taocalligraphy.ui.chat.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ImageSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemChatConversationBinding


class ChatConversationAdapter(val chatConversationClickListener: ChatConversationClickListener) :
    RecyclerView.Adapter<ChatConversationAdapter.ViewHolder>() {

    var isEdit = false
    var itemList = booleanArrayOf(false, false, false, false, false)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemChatConversationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.adapterPosition % 2 == 0) {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.gold_10
                )
            )
        } else {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.white
                )
            )
        }

        if (isEdit) {
            holder.item.ivSelect.isVisible = true
            holder.item.swipeLayout.animateReset()
            holder.item.swipeLayout.isLeftSwipeEnabled = false
            holder.item.swipeLayout.isRightSwipeEnabled = false
            val set = ConstraintSet()
            set.clone(holder.item.constraintMessage)
            set.clear(R.id.relativeMessage, ConstraintSet.END)
            set.applyTo(holder.item.constraintMessage)
        } else {
            holder.item.swipeLayout.animateReset()
            holder.item.swipeLayout.isLeftSwipeEnabled = true
            holder.item.swipeLayout.isRightSwipeEnabled = true
            holder.item.ivSelect.isGone = true
            val set = ConstraintSet()
            set.clone(holder.item.constraintMessage)
            set.connect(
                R.id.relativeMessage,
                ConstraintSet.END,
                ConstraintSet.PARENT_ID,
                ConstraintSet.END,
                0
            )
            set.applyTo(holder.item.constraintMessage)
        }

        if (itemList[holder.adapterPosition]) {
            holder.item.ivSelect.setImageResource(R.drawable.vd_ellipse_tick)
        } else {
            holder.item.ivSelect.setImageResource(R.drawable.vd_ellipse_blank)
        }

        val spannableText =
            SpannableStringBuilder(holder.itemView.context.getString(R.string.lorem_ipsum_dummy_text))
        holder.item.tvLastMessage.setText(spannableText, TextView.BufferType.SPANNABLE)
        holder.item.ivUserProfile.isGone = true
        holder.item.llGroupChatUserImage.isGone = true
        holder.item.flTwoUser.isGone = true
        holder.item.ivNotificationOff.isGone = true

        when (holder.adapterPosition) {
            0 -> {
                holder.item.ivUserProfile.isVisible = true
                holder.item.txtUserName.text = "Thomas Leimert"
            }
            1 -> {
                holder.item.llGroupChatUserImage.isVisible = true
                holder.item.txtUserName.text = "Thomas, Lennard, +3"
            }
            2 -> {
                holder.item.ivUserProfile.isVisible = true
                val spannableText =
                    SpannableStringBuilder(" " + holder.itemView.context.getString(R.string.lorem_ipsum_dummy_text)).apply {
                        val imageSpan =
                            ImageSpan(holder.itemView.context, R.drawable.vd_message_read)
                        setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                holder.item.tvLastMessage.setText(spannableText, TextView.BufferType.SPANNABLE)
                holder.item.txtUserName.text = "Thomas Leimert"
            }
            3 -> {
                holder.item.flTwoUser.isVisible = true
                val spannableText =
                    SpannableStringBuilder(" " + holder.itemView.context.getString(R.string.lorem_ipsum_dummy_text)).apply {
                        val imageSpan =
                            ImageSpan(holder.itemView.context, R.drawable.vd_message_unread)
                        setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                    }
                holder.item.tvLastMessage.setText(spannableText, TextView.BufferType.SPANNABLE)
                holder.item.txtUserName.text = "Elizabeth, Eleanor"
            }
            4 -> {
                holder.item.ivUserProfile.isVisible = true
                holder.item.ivNotificationOff.isVisible = true
                holder.item.txtUserName.text = "Thomas Leimert"
            }
        }

        holder.item.llMain.setOnClickListener {
            if (isEdit) {
                itemList[holder.adapterPosition] = !itemList[holder.adapterPosition]
                notifyItemChanged(holder.adapterPosition)
            } else {
                chatConversationClickListener.onConversationClick()
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ViewHolder(view: ItemChatConversationBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ChatConversationClickListener {
        fun onConversationClick()
    }
}