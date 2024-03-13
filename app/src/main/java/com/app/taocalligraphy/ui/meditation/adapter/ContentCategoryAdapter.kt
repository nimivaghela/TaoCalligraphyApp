package com.app.taocalligraphy.ui.meditation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemContentCategoryBinding

class ContentCategoryAdapter : RecyclerView.Adapter<ContentCategoryAdapter.ViewHolder>() {

    var categoryList: ArrayList<String> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemContentCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.apply {
            txtContentCategory.text = categoryList[position]
        }
    }

    override fun getItemCount() = categoryList.size

    fun setCategoryListData(categoryList: ArrayList<String>) {
        this.categoryList = categoryList
        notifyDataSetChanged()
    }

    class ViewHolder(view: ItemContentCategoryBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}