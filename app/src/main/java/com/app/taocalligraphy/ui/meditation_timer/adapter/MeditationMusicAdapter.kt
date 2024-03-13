package com.app.taocalligraphy.ui.meditation_timer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSoundSelectionListBinding
import com.app.taocalligraphy.models.response.meditation_timer.SoundApiResponse
import kotlinx.android.synthetic.main.item_sound_selection_list.view.*

class MeditationMusicAdapter(private var arrayList: MutableList<SoundApiResponse.SoundList?>) :
    RecyclerView.Adapter<MeditationMusicAdapter.EventDataViewHolder>() {

    lateinit var context: Context
    lateinit var select: OnSelectSoundItem
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventDataViewHolder {
        val inflator = LayoutInflater.from(parent.context)
        context = parent.context
        val binding = DataBindingUtil.inflate<ItemSoundSelectionListBinding>(
            inflator,
            R.layout.item_sound_selection_list,
            parent,
            false
        )
        binding.onSelectSoundItem = select
        return EventDataViewHolder(binding)
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: EventDataViewHolder, position: Int) {
        holder.onBindView(arrayList[position])
        if (arrayList[position]?.isSelected!!) {
            holder.itemView.llMain.setBackgroundResource(R.drawable.bg_gold_5dp)
            holder.itemView.imgTone.setColorFilter(ContextCompat.getColor(context, R.color.white))
            holder.itemView.tvSoundName.setTextColor(context.resources.getColor(R.color.white))
        } else {
            holder.itemView.llMain.setBackgroundResource(R.drawable.bg_white_gold_border)
            holder.itemView.imgTone.setColorFilter(
                ContextCompat.getColor(context, R.color.gold)
            )
            holder.itemView.tvSoundName.setTextColor(context.resources.getColor(R.color.gold))
        }
    }

    fun updateList(productList: ArrayList<SoundApiResponse.SoundList?>) {
        arrayList.clear()
        arrayList.addAll(productList)
        notifyDataSetChanged()
    }

    class EventDataViewHolder(private var binding: ItemSoundSelectionListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBindView(SoundApiResponse: SoundApiResponse.SoundList?) {
            binding.soundApiResponse = SoundApiResponse
            binding.position = adapterPosition
        }
    }

    interface OnSelectSoundItem {
        fun onSelectSoundItem(position: Int)
    }

}