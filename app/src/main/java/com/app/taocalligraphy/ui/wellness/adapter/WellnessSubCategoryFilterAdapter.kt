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
import com.app.taocalligraphy.utils.loadImage

class WellnessSubCategoryFilterAdapter(
    private val mListener: OnAdapterItemClickListener
) : RecyclerView.Adapter<WellnessSubCategoryFilterAdapter.ItemViewHolder>() {
    var selectedPosition = -1
    var list: MutableList<CategoryDataModel.SubCategoryDetail?> = arrayListOf()

    var previousSelected: ItemSelectSubCategoryBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_select_sub_category,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(list[position]) {
                tvCategoryTitle.text = this?.name ?: ""

                if (this?.isSelected == false) {
                    ivCategoryImage.loadImage(
                        this.icon,
                        R.drawable.ic_category_default
                    )
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.dark_grey
                        )
                    )
                } else {
                    ivCategoryImage.loadImage(
                        this?.selectedIcon,
                        R.drawable.ic_category_default
                    )
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.gold
                        )
                    )

                    list[position]!!.isSelected = true
                    selectedPosition = position
                    previousSelected = holder.binding
                }
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemSelectSubCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                previousSelected?.let {
                    it.ivCategoryImage.loadImage(
                        list[selectedPosition]?.icon,
                        R.drawable.ic_category_default
                    )
                    it.tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            it.tvCategoryTitle.context,
                            R.color.dark_grey
                        )
                    )
                    list[selectedPosition]!!.isSelected = false
                }

                if(selectedPosition == bindingAdapterPosition){
                    list[bindingAdapterPosition]!!.isSelected = false
                    selectedPosition = -1
                    previousSelected = null

                    binding.ivCategoryImage.loadImage(
                        list[bindingAdapterPosition]?.icon,
                        R.drawable.ic_category_default
                    )
                    binding.tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            binding.tvCategoryTitle.context,
                            R.color.dark_grey
                        )
                    )
                }else {
                    list[bindingAdapterPosition]!!.isSelected = true
                    selectedPosition = bindingAdapterPosition
                    previousSelected = binding
                    binding.ivCategoryImage.loadImage(
                        list[bindingAdapterPosition]?.selectedIcon,
                        R.drawable.ic_category_default
                    )
                    binding.tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            binding.tvCategoryTitle.context,
                            R.color.gold
                        )
                    )

                }


                list[bindingAdapterPosition]?.let { it1 -> mListener.onAdapterClick(it1) }
            }
        }
    }

    interface OnAdapterItemClickListener {
        fun onAdapterClick(mClickedData: CategoryDataModel.SubCategoryDetail)
    }
}