package com.app.taocalligraphy.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreVerticalProgressBinding
import com.app.taocalligraphy.databinding.ItemProgramsHistoryListBinding
import com.app.taocalligraphy.models.response.history.FetchProgramHistoryData
import com.app.taocalligraphy.utils.loadImage
import kotlinx.coroutines.flow.combine

class CompletedProgramsHistoryAdapter(var onItemClickListener: OnCompletedProgramItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<FetchProgramHistoryData.ForYouProgramData?> = arrayListOf()

    fun setCompletedPrograms(list: ArrayList<FetchProgramHistoryData.ForYouProgramData?>) {
        this.list = list
        notifyDataSetChanged()
    }

    private val ITEM_LOAD = 0
    private val ITEM_DATA = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_LOAD) {
            ProgressViewHolder(
                ItemLoadMoreVerticalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            DataHolder(
                ItemProgramsHistoryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataHolder) {
            holder.binding.apply {
                list[position]?.let { data ->
                    ivProgram.loadImage(
                        data.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )
                    tvProgressCount.text = data.completedPercentage.toString() + "%"
                    tvTitle.text = data.title
                    tvDate.text = data.programCompletedDate
                    lblDate.text = holder.itemView.context.getString(R.string.completed_on)
                    circleProgressTrophy.progress = data.completedPercentage ?: 0
                }

                root.setOnClickListener {
                    onItemClickListener.onCompletedItemClicked(list[holder.bindingAdapterPosition])
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
        } else {
            holder.itemView.updateLayoutParams {
                height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null)
            ITEM_LOAD
        else
            ITEM_DATA
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DataHolder(var binding: ItemProgramsHistoryListBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ProgressViewHolder(var binding: ItemLoadMoreVerticalProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnCompletedProgramItemClickListener {
        fun onCompletedItemClicked(data: FetchProgramHistoryData.ForYouProgramData?)
    }
}