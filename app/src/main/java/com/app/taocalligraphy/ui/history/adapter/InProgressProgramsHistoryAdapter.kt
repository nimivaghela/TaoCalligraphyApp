package com.app.taocalligraphy.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemJournalTitleSectionBinding
import com.app.taocalligraphy.databinding.ItemLoadMoreVerticalProgressBinding
import com.app.taocalligraphy.databinding.ItemProgramsHistoryListBinding
import com.app.taocalligraphy.models.response.history.FetchProgramHistoryData
import com.app.taocalligraphy.ui.notification.OnItemClicked
import com.app.taocalligraphy.utils.loadImage

class InProgressProgramsHistoryAdapter(
    var onItemClicked: OnInProgressProgramItemClickListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<FetchProgramHistoryData.ForYouProgramData?> = arrayListOf()

    fun setHistoryInProgressPrograms(list: ArrayList<FetchProgramHistoryData.ForYouProgramData?>) {
        this.list = list
        notifyDataSetChanged()
    }

    private val ITEM_LOAD = 0
    private val ITEM_DATA = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_LOAD) {
            val itemBindingUtil =
                ItemLoadMoreVerticalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ProgressViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemProgramsHistoryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null)
            ITEM_LOAD
        else
            ITEM_DATA
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProgressViewHolder) {
            holder.itemView.updateLayoutParams {
                height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
            }
        } else if (holder is DataHolder) {
            holder.binding.apply {
                list[position]?.let { data ->
                    ivProgram.loadImage(
                        data.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )
                    tvProgressCount.text = data.completedPercentage.toString() + "%"
                    tvTitle.text = data.title
                    tvDate.text = data.programJoinedDate
                    lblDate.text = holder.itemView.context.getString(R.string.started_on)
                    circleProgressTrophy.progress = data.completedPercentage ?: 0
                }

                root.setOnClickListener {
                    onItemClicked.onInProgressItemClicked(list[holder.bindingAdapterPosition])
                }
                if ((holder.bindingAdapterPosition) % 2 == 1) {
                    clMain.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            R.color.medium_grey_10
                        )
                    )
                } else {
                    clMain.setBackgroundColor(
                        ContextCompat.getColor(
                            holder.itemView.context,
                            android.R.color.transparent
                        )
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DataHolder(var binding: ItemProgramsHistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    class ProgressViewHolder(var binding: ItemLoadMoreVerticalProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface OnInProgressProgramItemClickListener {
        fun onInProgressItemClicked(data: FetchProgramHistoryData.ForYouProgramData?)
    }
}