package com.app.taocalligraphy.ui.wellness.adapter

import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemCategoryListBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class CategoryListAdapter(
    private var mDataList: MutableList<CategoryDataModel>,
    private val mListener: OnCategoryClicked
) : RecyclerView.Adapter<CategoryListAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemCategoryListBinding.inflate(
                LayoutInflater.from(parent.context.applicationContext),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvWellnessHealth.text = categoryName
                ivCategory.loadImage(smallIcon, R.drawable.ic_category_default)
                if((UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false) == false){
                    ivLock.visible()
                }else{
                    ivLock.gone()
                }
            }
        }
    }

    inner class ItemViewHolder(var binding: ItemCategoryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                mListener.onCategoryClicked(
                    bindingAdapterPosition
                )
            }
        }
    }

    interface OnCategoryClicked {
        fun onCategoryClicked(position: Int)
    }
}