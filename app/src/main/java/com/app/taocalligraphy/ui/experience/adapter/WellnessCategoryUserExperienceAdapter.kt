package com.app.taocalligraphy.ui.experience.adapter

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSelectCategoryBinding
import com.app.taocalligraphy.models.response.Category
import com.app.taocalligraphy.utils.extensions.clickWithDebounce
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class WellnessCategoryUserExperienceAdapter(
    private var mDataList: List<Category>,
    private var selectedId : Int,
    private val onCategoryClickListener: OnCategoryClickListener
) : RecyclerView.Adapter<WellnessCategoryUserExperienceAdapter.WellnessCategoryAdapterViewHolder>() {

    private var previousSelected: ItemSelectCategoryBinding? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WellnessCategoryAdapterViewHolder {
        return WellnessCategoryAdapterViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_select_category,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: WellnessCategoryAdapterViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                tvCategoryTitle.text = categoryName ?: ""
                ivCategoryImage.loadImage(
                    icon,
                    R.drawable.bg_grey_round
                )
                ivCategoryImageSelected.loadImage(
                    selectedIcon,
                    R.drawable.bg_grey_round
                )

                if(categoryId == selectedId){
                    ivCategoryImage.gone()
                    ivCategoryImageSelected.visible()
                    tvCategoryTitle.setTextColor(
                        ColorStateList.valueOf(
                            tvCategoryTitle.context.getColor(
                                R.color.gold
                            )
                        )
                    )
                    previousSelected = this@apply
                }

                rlCategory.clickWithDebounce {
                    previousSelected?.let {
                        it.ivCategoryImageSelected.gone()
                        it.ivCategoryImage.visible()
                        it.tvCategoryTitle.setTextColor(
                            ColorStateList.valueOf(
                                it.tvCategoryTitle.context.getColor(
                                    R.color.dark_grey
                                )
                            )
                        )
                    }

                    ivCategoryImage.gone()
                    ivCategoryImageSelected.visible()
                    tvCategoryTitle.setTextColor(
                        ColorStateList.valueOf(
                            tvCategoryTitle.context.getColor(
                                R.color.gold
                            )
                        )
                    )
                    if (previousSelected != null && previousSelected != this@apply) {
                        onCategoryClickListener.onCategoryClicked(categoryId)
                    }

                    if (previousSelected == null) {
                        onCategoryClickListener.onCategoryClicked(categoryId)
                    }
                    previousSelected = this@apply
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class WellnessCategoryAdapterViewHolder(var binding: ItemSelectCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }
}

interface OnCategoryClickListener {
    fun onCategoryClicked(categoryId: Int)
}