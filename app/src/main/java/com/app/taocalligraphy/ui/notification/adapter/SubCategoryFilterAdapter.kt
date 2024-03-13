package com.app.taocalligraphy.ui.notification.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_select_category_filter.view.*

class SubCategoryFilterAdapter :
    RecyclerView.Adapter<SubCategoryFilterAdapter.ViewHolder>() {

    private var subCategoryList = ArrayList<CategoryDataModel.SubCategoryDetail>()
    var selectedSubCategoryIds = ArrayList<Int>()

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_category_filter, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvCategoryTitle.text = subCategoryList[position].name ?: ""
        holder.itemView.ivCategoryImage.loadImage(
            subCategoryList[position].icon,
            R.drawable.ic_category_default
        )
        holder.itemView.ivCategorySelectedImage.loadImage(
            subCategoryList[position].selectedIcon,
            R.drawable.ic_category_default
        )
        if (selectedSubCategoryIds.contains(subCategoryList[position].id)) {
            holder.itemView.tvCategoryTitle.setTextColor(
                ContextCompat.getColor(
                    holder.itemView.tvCategoryTitle.context,
                    R.color.dark_grey
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
            if (selectedSubCategoryIds.contains(subCategoryList[position].id)) {
                subCategoryList[position].isSelected = false
                holder.itemView.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.tvCategoryTitle.context,
                        R.color.dark_grey
                    )
                )
                holder.itemView.ivCategorySelectedImage.gone()
                selectedSubCategoryIds.remove(subCategoryList[position].id)
            } else {
                subCategoryList[position].isSelected = true
                holder.itemView.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.tvCategoryTitle.context,
                        R.color.dark_grey
                    )
                )
                holder.itemView.ivCategorySelectedImage.visible()
                selectedSubCategoryIds.add(subCategoryList[position].id)
            }
        }
    }

    override fun getItemCount(): Int {
        return subCategoryList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun updateList(
        subCategoryList: ArrayList<CategoryDataModel.SubCategoryDetail>,
        selectedSubCategoryIds: ArrayList<Int>
    ) {
        this.subCategoryList = subCategoryList
        this.selectedSubCategoryIds = selectedSubCategoryIds
        notifyDataSetChanged()
    }
}