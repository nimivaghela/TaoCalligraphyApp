package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSelectCategorySmallBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_list_data.CategoryDataModel
import com.app.taocalligraphy.ui.program.viewmodel.ProgramViewModel
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.loadImage

class MeditationRoomCatSmallAdapter(
    val categorySelectionListener: CategorySelectionListener,
    val mViewModel: ProgramViewModel
) : RecyclerView.Adapter<MeditationRoomCatSmallAdapter.ItemViewHolder>() {

    private var categoryList = ArrayList<CategoryDataModel>()
    var previousSelected: ItemSelectCategorySmallBinding? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setCategoryData(categoriesList: ArrayList<CategoryDataModel>) {
        categoryList = categoriesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_select_category_small, parent, false
            )
        )
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {

        holder.binding.apply {
            val category = categoryList[position]
            category.let {
                tvCategoryTitle.text = it.categoryName
                ivCategoryImage.loadImage(
                    it.icon,
                    R.drawable.ic_category_default
                )
                ivFeaturedBg.gone()
                ivFeaturedImage.gone()
                if (mViewModel.selectedPosition == position) {
                    ivCategoryImage.loadImage(
                        category.selectedIcon,
                        R.drawable.ic_category_default
                    )
                    mViewModel.selectedPosition = position
                    previousSelected = holder.binding
                } else {
                    ivCategoryImage.loadImage(
                        category.icon,
                        R.drawable.ic_category_default
                    )
                }
            }

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemSelectCategorySmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.clickWithDebounce {
                if (mViewModel.selectedPosition != bindingAdapterPosition) {

                    previousSelected?.ivCategoryImage?.loadImage(
                        categoryList[mViewModel.selectedPosition].icon,
                        R.drawable.ic_category_default
                    )

                    categorySelectionListener.onCategoryChange(
                        categoryList[bindingAdapterPosition].categoryId ?: ""
                    )
                    mViewModel.selectedPosition = bindingAdapterPosition
                    previousSelected = binding

                    binding.ivCategoryImage.loadImage(
                        categoryList[bindingAdapterPosition].selectedIcon,
                        R.drawable.ic_category_default
                    )
                }
            }
        }
    }

    interface CategorySelectionListener {
        fun onCategoryChange(categoryId: String)
    }
}