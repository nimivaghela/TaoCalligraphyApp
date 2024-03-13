package com.app.taocalligraphy.ui.history.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemJournalTitleSectionBinding
import com.app.taocalligraphy.databinding.ItemLiveSessionsHistoryListBinding

class LiveSessionsHistoryAdapter(
    private var context: Context
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_TITLE = 0
    val ITEM_DATA = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == ITEM_TITLE) {
            val itemBindingUtil =
                ItemJournalTitleSectionBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            SectionTitleViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemLiveSessionsHistoryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM_TITLE
        } else {
            ITEM_DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SectionTitleViewHolder) {
            holder.item.tvJournalSectionTitle.text = context.getString(R.string.eight_sessions)
            holder.item.tvSwipeInfo.isGone = true
        } else if (holder is DataHolder) {
            if ((holder.adapterPosition + 1) % 2 == 0) {
                holder.item.clMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        android.R.color.transparent
                    )
                )
            } else {
                holder.item.clMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey_80
                    )
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return 20
    }

    class DataHolder(view: ItemLiveSessionsHistoryListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class SectionTitleViewHolder(view: ItemJournalTitleSectionBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}