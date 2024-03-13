package com.app.taocalligraphy.ui.history.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLightTransmissionsHistoryListBinding
import com.app.taocalligraphy.databinding.ItemLightTransmissionsOrderItemListBinding

class LightTransmissionsOrderItemAdapter() :
    RecyclerView.Adapter<LightTransmissionsOrderItemAdapter.DataHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataHolder {
        val itemBindingUtil =
            ItemLightTransmissionsOrderItemListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DataHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: DataHolder, position: Int) {
        if (holder.adapterPosition==itemCount-1){
            holder.item.viewSeprator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.medium_grey))
        }else{
            holder.item.viewSeprator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context,R.color.medium_grey_40))
        }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class DataHolder(view: ItemLightTransmissionsOrderItemListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}