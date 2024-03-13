package com.app.taocalligraphy.ui.downloads.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemDownlodedCategoryBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.ui.downloads.DownloadViewModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class DownloadedCategoryAdapter(val categoryClickListener: CategoryClickListener, private val viewModel: DownloadViewModel) :
    RecyclerView.Adapter<DownloadedCategoryAdapter.ViewHolder>() {

    var categoryList = ArrayList<CategoryDataModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemDownlodedCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categoryList[holder.bindingAdapterPosition]
        holder.item.ivCategory.loadImage(
            category.smallIcon,
            R.drawable.ic_category_default
        )
        holder.item.tvCategoryName.text = category.categoryName
        holder.item.viewDividerTop.gone()
        holder.item.viewDividerBottom.visible()
        if (holder.bindingAdapterPosition == 0) {
            holder.item.viewDividerTop.visible()
        }
        if (holder.bindingAdapterPosition == (categoryList.size - 1)) {
            holder.item.viewDividerBottom.gone()
        }
        holder.itemView.setOnClickListener {
            viewModel.selectedPosition = holder.bindingAdapterPosition
            categoryClickListener.onCategoryClick(holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }

    class ViewHolder(view: ItemDownlodedCategoryBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface CategoryClickListener {
        fun onCategoryClick(adapterPosition: Int)
    }
}