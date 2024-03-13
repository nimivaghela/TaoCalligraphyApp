package com.app.taocalligraphy.ui.meditation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ViewTagsItemBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.ui.meditation.viewmodel.MeditationContentViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce

class TagsAdapter(private val viewModel:MeditationContentViewModel?) : RecyclerView.Adapter<TagsAdapter.ViewHolder>() {

    private var tagsList: List<MeditationContentResponse.FeedbackTag> = ArrayList()

    fun updateTagList(tags: List<MeditationContentResponse.FeedbackTag>) {
        tagsList = tags
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: ViewTagsItemBinding) : RecyclerView.ViewHolder(itemView.root) {
        val item = itemView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ViewTagsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val feedbackTag = tagsList[position]
        holder.item.txtTags.text = feedbackTag.name

        if (viewModel?.selectedExperience?.contains(feedbackTag) == true) {
            holder.item.txtTags.setBackgroundResource(R.drawable.bg_tab_background_select)
            holder.item.txtTags.setTextColor(
                ContextCompat.getColor(
                    holder.item.txtTags.context,
                    R.color.white
                )
            )
        } else {
            holder.item.txtTags.setBackgroundResource(R.drawable.bg_tab_background_default)
            holder.item.txtTags.setTextColor(
                ContextCompat.getColor(
                    holder.item.txtTags.context,
                    R.color.dark_grey
                )
            )
        }

        holder.item.root.setOnClickListener {
            if (viewModel?.selectedExperience?.contains(feedbackTag) == true) {
                holder.item.txtTags.setBackgroundResource(R.drawable.bg_tab_background_default)
                holder.item.txtTags.setTextColor(
                    ContextCompat.getColor(
                        holder.item.txtTags.context,
                        R.color.dark_grey
                    )
                )
                viewModel.selectedExperience.remove(feedbackTag)
            } else {
                viewModel?.selectedExperience?.add(feedbackTag)
                holder.item.txtTags.setBackgroundResource(R.drawable.bg_tab_background_select)
                holder.item.txtTags.setTextColor(
                    ContextCompat.getColor(
                        holder.item.txtTags.context,
                        R.color.white
                    )
                )
            }
        }
    }

    override fun getItemCount() = tagsList.size
}