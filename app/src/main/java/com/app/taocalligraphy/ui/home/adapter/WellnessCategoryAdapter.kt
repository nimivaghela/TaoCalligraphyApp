package com.app.taocalligraphy.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSelectCategoryBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class WellnessCategoryAdapter(
    private val mListener: OnItemClickListener
) : RecyclerView.Adapter<WellnessCategoryAdapter.ItemViewHolder>() {

    var selectedPosition = -1

    private var mDataList: ArrayList<CategoryDataModel> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setCategoryData(dataList: ArrayList<CategoryDataModel>) {
        mDataList = dataList
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedCategoryPosition(selectedPos: Int) {
        selectedPosition = selectedPos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_select_category,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvCategoryTitle.text = categoryName ?: ""

                ivCategoryImage.loadImage(
                    icon,
                    R.drawable.ic_category_default
                )

                ivCategoryImageSelected.loadImage(
                    selectedIcon,
                    R.drawable.ic_category_default
                )

                if (isFeatured) {
                    ivFeaturedBg.visible()
                    ivFeaturedImage.visible()
                } else {
                    ivFeaturedBg.gone()
                    ivFeaturedImage.gone()
                }
                if (selectedPosition == holder.bindingAdapterPosition) {
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.gold
                        )
                    )
                    ivCategoryImage.gone()
                    ivCategoryImageSelected.visible()
                } else {
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.dark_grey
                        )
                    )
                    ivCategoryImage.visible()
                    ivCategoryImageSelected.gone()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemSelectCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(!(UserHolder.EnumUserModulePermission.CONTENT_LIBRARY.permission?.canAccess ?: false)){
                    return@setOnClickListener
                }

                selectedPosition = bindingAdapterPosition
                mListener.onItemCategoryClick(selectedPosition)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemCategoryClick(position: Int)
    }
}