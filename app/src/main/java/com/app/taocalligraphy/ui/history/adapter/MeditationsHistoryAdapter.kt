package com.app.taocalligraphy.ui.history.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreVerticalProgressBinding
import com.app.taocalligraphy.databinding.ItemMeditationsHistoryListBinding
import com.app.taocalligraphy.models.response.history.FetchMeditationHistoryData
import com.app.taocalligraphy.utils.loadImage

class MeditationsHistoryAdapter(var onMeditationItemClickListener: OnMeditationItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<FetchMeditationHistoryData.MeditationData?> = arrayListOf()

    fun setHistoryMeditationData(list: ArrayList<FetchMeditationHistoryData.MeditationData?>) {
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
                ItemMeditationsHistoryListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            ITEM_LOAD
        } else {
            ITEM_DATA
        }

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
                        data.thumbnailImage, R.drawable.img_default_for_content,
                        true
                    )
                    tvTitle.text = data.contentName
                    tvDate.text = data.contentLastPlayDate
                    var categoryName = ""

                    data.categoryDetailsList?.forEach {
                        categoryName += it?.mainCategory + ", " + TextUtils.join(
                            ", ",
                            it?.subCategory ?: arrayListOf<String>()
                        )

                    }
                    categoryName = categoryName.trim()

                    if (categoryName.substring(categoryName.length - 1, categoryName.length)
                            .contains(",")
                    ) {
                        categoryName = categoryName.replace(
                            categoryName.substring(categoryName.length - 1),
                            ""
                        )
                    }

                    tvCategory.text = categoryName

                    var contentPlayedTime = 0
                    var contentTotalTime = 0

                    data.contentLastPlayTime?.let {
                        val dataDate = it.split(":")
                        val hours = dataDate[0]
                        val minutes = dataDate[1]
                        val seconds = dataDate[2]
                        contentPlayedTime =
                            (hours.toInt() * 60 * 60) + (minutes.toInt() * 60) + seconds.toInt()
                    }

                    data.contentDuration?.let {
                        val dataDate = it.split(":")
                        val hours = dataDate[0]
                        val minutes = dataDate[1]
                        val seconds = dataDate[2]
                        contentTotalTime =
                            (hours.toInt() * 60 * 60) + (minutes.toInt() * 60) + seconds.toInt()
                    }
                    linearProgressMeditations.max = contentTotalTime
                    linearProgressMeditations.setProgressCompat(contentPlayedTime, true)
                }
                root.setOnClickListener {
                    onMeditationItemClickListener.onMeditationItemClicked(list[holder.bindingAdapterPosition])
                }
            }
            if ((holder.bindingAdapterPosition + 1) % 2 == 0) {
                holder.binding.clMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        android.R.color.transparent
                    )
                )
            } else {
                holder.binding.clMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.grey_80
                    )
                )
            }

        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DataHolder(var binding: ItemMeditationsHistoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    class ProgressViewHolder(var binding: ItemLoadMoreVerticalProgressBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface OnMeditationItemClickListener {
        fun onMeditationItemClicked(data: FetchMeditationHistoryData.MeditationData?)
    }
}