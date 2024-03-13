package com.app.taocalligraphy.ui.experience.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemVideoListExperienceMoreBinding
import com.app.taocalligraphy.models.response.ContentDetail
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.loadImage

class ContentDataAdapter(
    private var mDataList: List<ContentDetail>
) : RecyclerView.Adapter<ContentDataAdapter.AudioVideoDataAdapterViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AudioVideoDataAdapterViewHolder {
        return AudioVideoDataAdapterViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_video_list_experience_more,
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: AudioVideoDataAdapterViewHolder, position: Int) {
        holder.binding.apply {
            ivPlay.gone()
            with(mDataList[position]) {
                tvAudioVideoName.text = title ?: ""

                ivThumbImage.loadImage(
                    thumbnailImage,
                    R.drawable.img_default_for_content,
                    true
                )
            }
        }

    }

    inner class AudioVideoDataAdapterViewHolder(var binding: ItemVideoListExperienceMoreBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}