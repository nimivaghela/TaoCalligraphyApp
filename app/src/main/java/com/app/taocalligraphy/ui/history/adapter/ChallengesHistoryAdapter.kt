package com.app.taocalligraphy.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.*
import com.app.taocalligraphy.ui.journal.adapter.JournalListAdapter

class ChallengesHistoryAdapter() :
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
                ItemChallengesHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if ((position == 0) or (position == 3)) {
            ITEM_TITLE
        } else {
            ITEM_DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is SectionTitleViewHolder){
            if (holder.adapterPosition==0){
                holder.item.tvJournalSectionTitle.text=holder.itemView.context.getString(R.string.in_progress)
            }else{
                holder.item.tvJournalSectionTitle.text=holder.itemView.context.getString(R.string.completed)
            }
            holder.item.tvSwipeInfo.isGone=true
        }else if (holder is DataHolder){
            if ((holder.adapterPosition+1)%3==0){
                holder.item.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.medium_grey_10))
            }else{
                holder.item.clMain.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,android.R.color.transparent))
            }
            if (holder.adapterPosition>2){
                holder.item.tvProgramDateTitle.text=holder.itemView.context.getString(R.string.completed_on)
            }else{
                holder.item.tvProgramDateTitle.text=holder.itemView.context.getString(R.string.started_on)
            }
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    class DataHolder(view: ItemChallengesHistoryListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class SectionTitleViewHolder(view: ItemJournalTitleSectionBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}