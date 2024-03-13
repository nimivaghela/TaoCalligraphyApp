package com.app.taocalligraphy.ui.field_healing.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemFieldHealingTextBinding

class HealingNamesAdapter(
    private var mDataList: Array<String>,
    private val mListener: OnAdapterItemClickListener
) : RecyclerView.Adapter<HealingNamesAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_field_healing_text,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvName.text = this
            }
        }

    }

    inner class ItemViewHolder(var binding: ItemFieldHealingTextBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.llRoot.setOnClickListener {
                mListener.onNameClick(mDataList[adapterPosition])
            }
        }
    }

    interface OnAdapterItemClickListener {
        fun onNameClick(mName: String)
    }
}