package com.app.taocalligraphy.ui.create_meditation_room.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemCreateRoomBannerImagesBinding

class CreateRoomImagesAdapter(
    private val mListener: OnImagesItemClickListener
) : RecyclerView.Adapter<CreateRoomImagesAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_create_room_banner_images,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.binding.apply {
            when (position) {
                0 -> {
                    cvMain.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvMain.context,
                            R.color.gold_semi_light
                        )
                    )
                    val mLayoutParams =
                        cvBackground.layoutParams as ViewGroup.MarginLayoutParams
                    mLayoutParams.setMargins(8, 8, 8, 8)
                    cvBackground.requestLayout()
                    ivBackgroundImage.setImageResource(R.drawable.ic_background_community_room)
                    ivLock.visibility = View.GONE
                }
                1 -> {
                    cvMain.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvMain.context,
                            R.color.shimmer_light
                        )
                    )
                    ivBackgroundImage.setImageResource(R.drawable.ic_background_tcf_rooms_2)
                    ivLock.visibility = View.GONE
                }
                2 -> {
                    cvMain.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvMain.context,
                            R.color.shimmer_light
                        )
                    )
                    ivBackgroundImage.setImageResource(R.drawable.ic_background_featured_room)
                    ivLock.visibility = View.GONE
                }
                3 -> {
                    cvMain.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvMain.context,
                            R.color.shimmer_light
                        )
                    )
                    ivBackgroundImage.setImageResource(R.drawable.ic_background_tcf_rooms_3)
                    ivLock.visibility = View.VISIBLE
                }
            }
        }
    }

    inner class ItemViewHolder(var binding: ItemCreateRoomBannerImagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                mListener.onImageClick(bindingAdapterPosition)
            }
        }
    }

    interface OnImagesItemClickListener {
        fun onImageClick(position: Int)
    }
}