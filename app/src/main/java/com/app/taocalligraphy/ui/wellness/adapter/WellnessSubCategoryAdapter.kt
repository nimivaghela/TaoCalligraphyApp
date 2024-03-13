package com.app.taocalligraphy.ui.wellness.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSelectSubCategoryBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.loadImage

class WellnessSubCategoryAdapter(
    val categorySelectionListener: CategorySelectionListener
) : RecyclerView.Adapter<WellnessSubCategoryAdapter.ItemViewHolder>() {

    private var selectedPosition = 0
    private var categoryList: List<CategoryDataModel.SubCategoryDetail> = ArrayList()
    private var previousSelected: ItemSelectSubCategoryBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_select_sub_category, parent, false
            )
        )
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            val category = categoryList[position]
            category.let {
                tvCategoryTitle.text = it.name
                ivCategoryImage.loadImage(
                    it.icon,
                    R.drawable.ic_category_default
                )

                if (selectedPosition == position) {
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.gold
                        )
                    )
                    ivCategoryImage.loadImage(
                        category.selectedIcon,
                        R.drawable.ic_category_default
                    )
                    selectedPosition = position
                    previousSelected = holder.binding
                } else {
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.dark_grey
                        )
                    )
                    ivCategoryImage.loadImage(
                        category.icon,
                        R.drawable.ic_category_default
                    )
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemSelectSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.clickWithDebounce {
                if (selectedPosition != bindingAdapterPosition) {
                    previousSelected?.let {
                        it.ivCategoryImage.loadImage(
                            categoryList[selectedPosition].icon,
                            R.drawable.ic_category_default
                        )
                        it.tvCategoryTitle.setTextColor(
                            ContextCompat.getColor(
                                it.tvCategoryTitle.context,
                                R.color.dark_grey
                            )
                        )
                    }

                    categorySelectionListener.onCategoryChange(
                        categoryList[bindingAdapterPosition].id,
                        categoryList[bindingAdapterPosition].name,
                        bindingAdapterPosition
                    )
                    selectedPosition = bindingAdapterPosition
                    previousSelected = binding

                    binding.ivCategoryImage.loadImage(
                        categoryList[bindingAdapterPosition].selectedIcon,
                        R.drawable.ic_category_default
                    )
                    binding.tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            binding.tvCategoryTitle.context,
                            R.color.gold
                        )
                    )
                }
            }
        }
    }

    fun setSubCategory(
        subCategories: List<CategoryDataModel.SubCategoryDetail>,
        selectedPosition: Int
    ) {
        this.categoryList = subCategories
        this.selectedPosition = selectedPosition
        notifyDataSetChanged()
    }

    interface CategorySelectionListener {
        fun onCategoryChange(categoryId: Int, categoryName: String, selectedPos: Int)
    }
}