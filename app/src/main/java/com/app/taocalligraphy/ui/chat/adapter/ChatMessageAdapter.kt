package com.app.taocalligraphy.ui.chat.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.setPadding
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.*
import com.app.taocalligraphy.models.IconNamePopupModel
import com.skydoves.powermenu.CustomPowerMenu


class ChatMessageAdapter(private val chatMessageListener: ChatMessageListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TIME_HEADER = 0
    private val SENDER_NAME_HEADER = 1
    private val OTHER_USER = 2
    private val LOGIN_USER = 3
    private val LOGIN_USER_AUDIO = 4
    private val OTHER_USER_AUDIO = 5

    //var customPowerMenu: CustomPowerMenu<IconNamePopupModel, CustomMenuItemAdapter>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            TIME_HEADER -> {
                val itemBindingUtil =
                    ItemChatTimeSectionHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return RelativeTimeHeaderViewHolder(itemBindingUtil)
            }
            SENDER_NAME_HEADER -> {
                val itemBindingUtil =
                    ItemChatUsernameSectionHeaderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return UserNameViewHolder(itemBindingUtil)
            }
            OTHER_USER -> {
                val itemBindingUtil =
                    ItemChatMessageSenderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return SenderViewHolder(itemBindingUtil)
            }
            LOGIN_USER_AUDIO -> {
                val itemBindingUtil =
                    ItemChatAudioReceiverBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return ReceiverAudioViewHolder(itemBindingUtil)
            }
            OTHER_USER_AUDIO -> {
                val itemBindingUtil =
                    ItemChatAudioSenderBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return SenderAudioViewHolder(itemBindingUtil)
            }
            else -> {
                val itemBindingUtil =
                    ItemChatMessageReceiverBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return ReceiverViewHolder(itemBindingUtil)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            (position == 0) or (position == 5) -> {
                TIME_HEADER
            }
            (position == 1) or (position == 6) or (position == 10) -> {
                SENDER_NAME_HEADER
            }
            (position == 2) or (position == 3) or (position == 7) or (position == 8) -> {
                OTHER_USER
            }
            (position == 9) -> {
                LOGIN_USER_AUDIO
            }
            (position == 11) -> {
                OTHER_USER_AUDIO
            }
            else -> {
                LOGIN_USER
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is RelativeTimeHeaderViewHolder) {
            if (holder.adapterPosition == 0) {
                holder.item.tvTime.text = "Sat, Feb 5"
            } else {
                holder.item.tvTime.text = holder.itemView.context.getString(R.string.today)
            }
        } else if (holder is ReceiverViewHolder) {
            holder.item.llMain.setOnClickListener {
                chatMessageListener.onChatMessageClick(
                    holder.item.tvMessage,
                    holder.adapterPosition
                )
            }
        } else if (holder is ReceiverAudioViewHolder) {
            if (holder.item.ivPlayAudio.tag == null) {
                holder.item.ivPlayAudio.tag = R.drawable.vd_play_gray
            }

            holder.item.ivPlayAudio.setOnClickListener {
                if (holder.item.ivPlayAudio.tag == R.drawable.vd_play_gray) {
                    holder.item.ivPlayAudio.tag = R.drawable.vd_pause_gray
                } else {
                    holder.item.ivPlayAudio.tag = R.drawable.vd_play_gray
                }
                holder.item.ivPlayAudio.setImageResource(holder.item.ivPlayAudio.tag as Int)
            }
            holder.item.llMain.setOnClickListener {
                chatMessageListener.onChatMessageClick(
                    holder.item.ivSoundWave,
                    holder.adapterPosition
                )
            }
        } else if (holder is SenderAudioViewHolder) {
            if (holder.item.ivPlayAudio.tag == null) {
                holder.item.ivPlayAudio.tag = R.drawable.vd_play_gray
            }

            holder.item.ivPlayAudio.setOnClickListener {
                if (holder.item.ivPlayAudio.tag == R.drawable.vd_play_gray) {
                    holder.item.ivPlayAudio.tag = R.drawable.vd_pause_gray
                } else {
                    holder.item.ivPlayAudio.tag = R.drawable.vd_play_gray
                }
                holder.item.ivPlayAudio.setImageResource(holder.item.ivPlayAudio.tag as Int)
            }
        }
    }

    override fun getItemCount(): Int {
        return 12
    }

    class SenderViewHolder(view: ItemChatMessageSenderBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ReceiverViewHolder(view: ItemChatMessageReceiverBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ReceiverAudioViewHolder(view: ItemChatAudioReceiverBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class SenderAudioViewHolder(view: ItemChatAudioSenderBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class RelativeTimeHeaderViewHolder(view: ItemChatTimeSectionHeaderBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class UserNameViewHolder(view: ItemChatUsernameSectionHeaderBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ChatMessageListener {
        fun onChatMessageClick(itemView: View, adapterPosition: Int)
    }
}