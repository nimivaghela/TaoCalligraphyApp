package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSelectCategorySmallBinding
import com.app.taocalligraphy.models.dummy_models.WellnessCategoryListModelDummy
import com.app.taocalligraphy.utils.loadImage

class WellnessCategorySmallAdapter(
    private var isFromProgranList: Boolean,
    private var mDataList: MutableList<WellnessCategoryListModelDummy>,
    private val mListener: OnAdapterItemClickListener
) : RecyclerView.Adapter<WellnessCategorySmallAdapter.ItemViewHolder>() {

    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_select_category_small, parent, false
            )
        )
    }

    override fun getItemCount(): Int = mDataList.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            with(mDataList[position]) {
                if (title!!.contains(" ")) {
                    tvCategoryTitle.maxLines = 2
                } else {
                    tvCategoryTitle.maxLines = 1
                }
                tvCategoryTitle.text = title ?: ""
                ivCategoryImage.loadImage(
                    image,
                    R.drawable.ic_category_default
                )
                if (selectedPosition == position) {
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.gold
                        )
                    )
                    when (position) {
                        0 -> {
                            ivCategoryImage.loadImage(
                                R.drawable.vd_health_icon_selected,
                                R.drawable.ic_category_default
                            )
                        }
                        1 -> {
                            ivCategoryImage.loadImage(
                                R.drawable.vd_relationships_icon_selected,
                                R.drawable.ic_category_default
                            )
                        }
                        2 -> {
                            ivCategoryImage.loadImage(
                                R.drawable.vd_peak_performance_icon_selected,
                                R.drawable.ic_category_default
                            )
                        }
                        3 -> {
                            ivCategoryImage.loadImage(
                                R.drawable.vd_business_finances_icon_selected,
                                R.drawable.ic_category_default
                            )
                        }
                        4 -> {
                            ivCategoryImage.loadImage(
                                R.drawable.vd_pregnancy_icon_selected,
                                R.drawable.ic_category_default
                            )
                        }
                        5 -> {
                            ivCategoryImage.loadImage(
                                R.drawable.vd_children_education_icon_selected,
                                R.drawable.ic_category_default
                            )
                        }
                    }
                } else {
                    tvCategoryTitle.setTextColor(
                        ContextCompat.getColor(
                            tvCategoryTitle.context,
                            R.color.dark_grey
                        )
                    )
                    /*when (position) {
                        0 -> {
                            Glide.with(ivCategoryImage).load(R.drawable.vd_health_icon).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round).into(ivCategoryImage)
                        }
                        1 -> {
                            Glide.with(ivCategoryImage).load(R.drawable.vd_relationships_icon).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round).into(ivCategoryImage)
                        }
                        2 -> {
                            Glide.with(ivCategoryImage).load(R.drawable.vd_peak_performance_icon).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round).into(ivCategoryImage)
                        }
                        3 -> {
                            Glide.with(ivCategoryImage).load(R.drawable.vd_business_finances_icon).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round).into(ivCategoryImage)
                        }
                        4 -> {
                            Glide.with(ivCategoryImage).load(R.drawable.vd_pregnancy_icon).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round).into(ivCategoryImage)
                        }
                        5 -> {
                            Glide.with(ivCategoryImage).load(R.drawable.vd_children_education_icon).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round).into(ivCategoryImage)
                        }
                    }*/
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    inner class ItemViewHolder(var binding: ItemSelectCategorySmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                /*when (adapterPosition) {
                    0 -> {
                        Glide.with(itemView.ivCategoryImage).load(R.drawable.vd_health_icon_selected).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round)
                            .into(itemView.ivCategoryImage)
                    }
                    1 -> {
                        Glide.with(itemView.ivCategoryImage).load(R.drawable.vd_relationships_icon_selected).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round)
                            .into(itemView.ivCategoryImage)
                    }
                    2 -> {
                        Glide.with(itemView.ivCategoryImage).load(R.drawable.vd_peak_performance_icon_selected).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round)
                            .into(itemView.ivCategoryImage)
                    }
                    3 -> {
                        Glide.with(itemView.ivCategoryImage).load(R.drawable.vd_business_finances_icon_selected).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round)
                            .into(itemView.ivCategoryImage)
                    }
                    4 -> {
                        Glide.with(itemView.ivCategoryImage).load(R.drawable.vd_pregnancy_icon_selected).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round)
                            .into(itemView.ivCategoryImage)
                    }
                    5 -> {
                        Glide.with(itemView.ivCategoryImage).load(R.drawable.vd_children_education_icon_selected).placeholder(R.drawable.bg_grey_round).error(R.drawable.bg_grey_round)
                            .into(itemView.ivCategoryImage)
                    }
                }*/
                mListener.onAdapterClick(mDataList[bindingAdapterPosition])
                selectedPosition = bindingAdapterPosition
                notifyDataSetChanged()
            }
        }
    }

    interface OnAdapterItemClickListener {
        fun onAdapterClick(mClickedData: WellnessCategoryListModelDummy)
    }
}