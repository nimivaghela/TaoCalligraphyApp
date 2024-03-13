package com.app.taocalligraphy.ui.notification.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.ui.playlist.viewmodel.PlaylistViewModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_select_category_filter.view.*

class CategoryFilterAdapter(
    private val onCategorySelectedListener: OnCategorySelectedListener,
    private val mViewModel: PlaylistViewModel
) :
    RecyclerView.Adapter<CategoryFilterAdapter.ViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_category_filter, parent, false)
        return ViewHolder(v)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.tvCategoryTitle.text =
            mViewModel.categoryDataList[position].categoryName ?: ""
        holder.itemView.ivCategoryImage.loadImage(
            mViewModel.categoryDataList[position].icon,
            R.drawable.ic_category_default
        )
        holder.itemView.ivCategorySelectedImage.loadImage(
            mViewModel.categoryDataList[position].selectedIcon,
            R.drawable.ic_category_default
        )

        holder.itemView.ivFeaturedBg.gone()
        holder.itemView.ivFeaturedImage.gone()

        if (mViewModel.categoryDataList[position].isSelected) {
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
            if (mViewModel.categoryDataList[position].isSelected) {
                onCategorySelectedListener.onCategorySelected(id = 0)

                it.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        it.tvCategoryTitle.context,
                        R.color.dark_grey
                    )
                )
                it.ivCategorySelectedImage.gone()
                mViewModel.categoryDataList[position].isSelected = false
            } else {
                it.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        it.tvCategoryTitle.context,
                        R.color.dark_grey
                    )
                )
                it.ivCategorySelectedImage.gone()

                holder.itemView.tvCategoryTitle.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.tvCategoryTitle.context,
                        R.color.dark_grey
                    )
                )
                holder.itemView.ivCategorySelectedImage.visible()
                mViewModel.categoryDataList[position].categoryId?.let { id ->
                    onCategorySelectedListener.onCategorySelected(id = id.toInt())
                }
                mViewModel.categoryDataList.map { category -> category.isSelected = false }
                mViewModel.categoryDataList[position].isSelected = true
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return mViewModel.categoryDataList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnCategorySelectedListener {
        fun onCategorySelected(id: Int)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList() {
        notifyDataSetChanged()
    }
}