package com.app.taocalligraphy.ui.meditation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemSubtitleBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.ui.meditation.dialog.ChooseSubTitleDialog

class SubtitleAdapter(private val subtitleSelectionListener: ChooseSubTitleDialog.SubtitleSelectionListener) :
    RecyclerView.Adapter<SubtitleAdapter.ViewHolder>() {

    var subtitleList: List<MeditationContentResponse.SubtitleWithLanguage?>? = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemSubtitleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val subtitle = subtitleList?.get(holder.bindingAdapterPosition)
        holder.item.tvSubTitle.text = subtitle?.languageName
        holder.item.tvSubTitle.setOnClickListener {
            subtitleSelectionListener.onSubtitleSelect(subtitle)
        }
    }

    override fun getItemCount(): Int {
        return subtitleList?.size ?: 0
    }

    class ViewHolder(view: ItemSubtitleBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}