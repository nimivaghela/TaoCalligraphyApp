package com.app.taocalligraphy.ui.profile.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemProfileMenuBinding
import com.app.taocalligraphy.models.ProfileMenuModel
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class ProfileMenuAdapter(
    var mDataList: MutableList<ProfileMenuModel>,
    private val mListener: OnAdapterItemClickListener
) : RecyclerView.Adapter<ProfileMenuAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemProfileMenuBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.item.apply {
            with(mDataList[position]) {
                ivImage.loadImage(
                    image,
                    R.drawable.bg_white_circle_gold_border
                )
                tvMenuName.text = title
                tvMenuDescription.text = description
                if (description.isNullOrEmpty()) {
                    tvMenuDescription.gone()
                } else {
                    tvMenuDescription.visible()
                }
                if (!this.canAccess) {
                    ivEnd.setBackgroundResource(R.drawable.ic_lock_gray)
                } else {
                    ivEnd.setBackgroundResource(R.drawable.vd_right_arrow_gold)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    inner class ViewHolder(view: ItemProfileMenuBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view

        init {
            view.root.setOnClickListener {
                mListener.onAdapterClick(mDataList[adapterPosition])
            }
        }

    }

    interface OnAdapterItemClickListener {
        fun onAdapterClick(mClickedData: ProfileMenuModel)
    }
}