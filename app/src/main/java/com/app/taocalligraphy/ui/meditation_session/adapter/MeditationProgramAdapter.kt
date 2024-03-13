package com.app.taocalligraphy.ui.meditation_session.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemBannerListBinding
import com.app.taocalligraphy.databinding.ItemMeditationProgramListBinding


class MeditationProgramAdapter() :
    RecyclerView.Adapter<MeditationProgramAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemMeditationProgramListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return 6
    }

    class ViewHolder(view: ItemMeditationProgramListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}