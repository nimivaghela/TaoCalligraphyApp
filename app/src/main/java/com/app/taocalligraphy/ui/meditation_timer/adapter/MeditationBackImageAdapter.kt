package com.app.taocalligraphy.ui.meditation_timer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSoundBackgroundListBinding
import com.app.taocalligraphy.models.response.meditation_timer.SoundBackImageApiResponse
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_sound_background_list.view.*

class MeditationBackImageAdapter(private var arrayList: SoundBackImageApiResponse) :
    RecyclerView.Adapter<MeditationBackImageAdapter.EventDataViewHolder>() {

    lateinit var context: Context
    lateinit var select: OnSelectSoundBackItem
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDataViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemSoundBackgroundListBinding>(
            inflator,
            R.layout.item_sound_background_list,
            parent,
            false
        )
        binding.onSelectSoundBackItem = select
        return EventDataViewHolder(binding)
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: EventDataViewHolder, position: Int) {
        holder.onBindView(arrayList)
        holder.itemView.imgBackgroundImage.loadImage(
            arrayList[position].backgroundImageOriginal,
             R.color.white
        )
        if (arrayList[position].isDefault == true) {
            holder.itemView.ivHostBorder.visible()
        } else {
            holder.itemView.ivHostBorder.gone()
        }
    }

    class EventDataViewHolder(private var binding: ItemSoundBackgroundListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBindView(SoundApiResponse: SoundBackImageApiResponse) {
            binding.soundBackApiResponse = SoundApiResponse
            binding.position = bindingAdapterPosition
        }
    }

    interface OnSelectSoundBackItem {
        fun onSelectSoundBackItem(position: Int)
    }

}