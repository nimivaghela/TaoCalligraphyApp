package com.app.taocalligraphy.ui.meditation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemVideoStepBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse

class MeditationVideoPlaylistAdapter(private val videoPlaylistItemClickListener: VideoPlaylistItemClickListener) :
    RecyclerView.Adapter<MeditationVideoPlaylistAdapter.ViewHolder>() {

    var currentMediaPlayPosition = 0
    var previousSelectedView: AppCompatTextView? = null
    var chapterList: List<MeditationContentResponse.Chapter?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemVideoStepBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chapterData = chapterList[position]
        chapterData?.let { chapter ->
            holder.item.tvStep.text = chapter.chapterName
            if (currentMediaPlayPosition == position) {
                previousSelectedView = holder.item.tvStep
                holder.item.tvStep.typeface =
                    ResourcesCompat.getFont(holder.item.tvStep.context, R.font.font_jost_bold)
            } else {
                holder.item.tvStep.typeface =
                    ResourcesCompat.getFont(holder.item.tvStep.context, R.font.font_jost_regular)
            }

            holder.itemView.setOnClickListener {
                if (currentMediaPlayPosition != position) {
                    holder.item.tvStep.typeface =
                        ResourcesCompat.getFont(holder.item.tvStep.context, R.font.font_jost_bold)

                    previousSelectedView?.typeface =
                        ResourcesCompat.getFont(
                            holder.item.tvStep.context,
                            R.font.font_jost_regular
                        )

                    currentMediaPlayPosition = position
                    previousSelectedView = holder.item.tvStep
                    videoPlaylistItemClickListener.onMediaChanged(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return chapterList.size
    }

    class ViewHolder(view: ItemVideoStepBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface VideoPlaylistItemClickListener {
        fun onMediaChanged(position: Int)
    }
}