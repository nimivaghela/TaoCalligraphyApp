package com.app.taocalligraphy.ui.meditation_rooms_detail.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemHostListBinding

class HostsListAdapter(
    private var context: Context
) : RecyclerView.Adapter<HostsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemHostListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (position) {
            0 -> {
                holder.item.ivHostImage.setImageResource(R.drawable.dummy_master_sha)
                holder.item.tvHostName.text = context.getString(R.string.dr_master_sha)
            }
            1 -> {
                holder.item.ivHostImage.setImageResource(R.drawable.dummy_master_francisco)
                holder.item.tvHostName.text = context.getString(R.string.francisco_quintero)
            }
            2 -> {
                holder.item.ivHostImage.setImageResource(R.drawable.dummy_master_david)
                holder.item.tvHostName.text = context.getString(R.string.david_lusch)
            }
            3 -> {
                holder.item.ivHostImage.setImageResource(R.drawable.dummy_master_cecilia)
                holder.item.tvHostName.text = context.getString(R.string.cecilia_liu)
            }
            4 -> {
                holder.item.ivHostImage.setImageResource(R.drawable.dummy_master_allan)
                holder.item.tvHostName.text = context.getString(R.string.allan_chuch)
            }
            5 -> {
                holder.item.ivHostImage.setImageResource(R.drawable.dummy_master_sher)
                holder.item.tvHostName.text = context.getString(R.string.sher)
            }
        }
    }

    override fun getItemCount(): Int {
        return 6
    }

    class ViewHolder(view: ItemHostListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}