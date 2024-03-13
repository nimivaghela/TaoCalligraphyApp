package com.app.taocalligraphy.ui.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_tags_by_id.FetchCategoryTagsByIdResponse
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_select_category_filter.view.*

class TagFilterAdapter :
    RecyclerView.Adapter<TagFilterAdapter.ViewHolder>() {

    private var tagList = ArrayList<CategoryDataModel>()
    var selectedTagList = ArrayList<Int>()

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_category_filter, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvCategoryTitle.text = tagList[position].categoryName ?: ""
        holder.itemView.ivCategoryImage.loadImage(
            tagList[position].icon,
            R.drawable.ic_category_default
        )
        holder.itemView.ivCategorySelectedImage.loadImage(
            tagList[position].selectedIcon,
            R.drawable.ic_category_default
        )
        if (selectedTagList.contains(tagList[position].categoryId?.toInt())) {
            holder.itemView.tvCategoryTitle.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.tvCategoryTitle.context,
                    R.color.gold
                )
            )
            holder.itemView.ivCategorySelectedImage.visible()
        } else {
            holder.itemView.tvCategoryTitle.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.tvCategoryTitle.context,
                    R.color.dark_grey
                )
            )
            holder.itemView.ivCategorySelectedImage.gone()
        }
        holder.itemView.setOnClickListener {
            if (selectedTagList.contains(tagList[position].categoryId?.toInt())) {
                tagList[position].isSelected = false
                holder.itemView.ivCategorySelectedImage.gone()
                holder.itemView.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.tvCategoryTitle.context,
                        R.color.dark_grey
                    )
                )
                selectedTagList.remove(tagList[position].categoryId?.toInt() ?: 0)
            } else {
                tagList[position].isSelected = true
                holder.itemView.ivCategorySelectedImage.visible()
                holder.itemView.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.tvCategoryTitle.context,
                        R.color.gold
                    )
                )

                selectedTagList.add(tagList[position].categoryId?.toInt() ?: 0)
            }
        }
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun updateList(tagList: ArrayList<CategoryDataModel>, selectedTagList: ArrayList<Int>) {
        this.tagList = tagList
        this.selectedTagList = selectedTagList
        notifyDataSetChanged()
    }
}