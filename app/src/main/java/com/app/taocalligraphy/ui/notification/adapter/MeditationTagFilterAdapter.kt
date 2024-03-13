package com.app.taocalligraphy.ui.notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.databinding.ItemMeditationCategoryListBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import kotlinx.android.synthetic.main.item_meditation_category_list.view.*

class MeditationTagFilterAdapter(val arrayList: MutableList<FetchCategoryTagsByIdResponse.TagsDetail>) :
    RecyclerView.Adapter<MeditationTagFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemMeditationCategoryListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvTitle.text = arrayList[position].name
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    class ViewHolder(view: ItemMeditationCategoryListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    fun updateList(addressList: MutableList<FetchCategoryTagsByIdResponse.TagsDetail>) {
        arrayList.clear()
        arrayList.addAll(addressList)
        notifyDataSetChanged()
    }

}