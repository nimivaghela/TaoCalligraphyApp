package com.app.taocalligraphy.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemJournalTitleSectionBinding
import com.app.taocalligraphy.databinding.ItemLightTransmissionsHistoryListBinding

class LightTransmissionsHistoryAdapter(
    private val lightTransmissionsItemClickListener: LightTransmissionsItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_TITLE = 0
    private val ITEM_DATA = 1


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SectionTitleViewHolder) {
            holder.item.tvJournalSectionTitle.isGone = true
            holder.item.tvSwipeInfo.isGone = true
        } else if (holder is DataHolder) {
            if ((holder.adapterPosition + 1) % 2 == 0) {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        android.R.color.transparent
                    )
                )
            } else {
                holder.item.rrMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.medium_grey_10
                    )
                )
            }
            holder.itemView.setOnClickListener {
                lightTransmissionsItemClickListener.onLightTransmissionItemClick()
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            ITEM_TITLE
        } else {
            ITEM_DATA
        }
    }

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
                ItemLightTransmissionsHistoryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemCount(): Int {
        return 10
    }

    class DataHolder(view: ItemLightTransmissionsHistoryListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface LightTransmissionsItemClickListener {
        fun onLightTransmissionItemClick()
    }

    class SectionTitleViewHolder(view: ItemJournalTitleSectionBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}