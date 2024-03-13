package com.app.taocalligraphy.ui.search.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemSearchTextBinding

class SearchTextAdapter(
    var itemClicked: OnItemListener
) : RecyclerView.Adapter<SearchTextAdapter.ViewHolder>() {

    var list: ArrayList<String> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setSearchData(dataList: ArrayList<String>) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemSearchTextBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (data.isNotEmpty())
            holder.item.tvName.text = data

        holder.item.root.setOnClickListener {

            itemClicked.onSearchTextItemClicked(data)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(view: ItemSearchTextBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnItemListener {
        fun onSearchTextItemClicked(data: String)
    }

}