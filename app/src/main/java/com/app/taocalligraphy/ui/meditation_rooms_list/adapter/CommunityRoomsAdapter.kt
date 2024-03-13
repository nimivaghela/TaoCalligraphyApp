package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemsCommunityRoomsBinding
import com.app.taocalligraphy.models.dummy_models.RoomDummyDataModel

class CommunityRoomsAdapter(
    private val mListener: OnCommunityRoomsItemClickListener
) : RecyclerView.Adapter<CommunityRoomsAdapter.ItemViewHolder>() {
    var list: ArrayList<RoomDummyDataModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.items_community_rooms,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = list[position]

        holder.binding.apply {
            flFavourite.setOnClickListener {
                if (data.isFavorite == true) {
                    lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                    lottieFavourite.progress = 1f
                    data.isFavorite = false
//                        categoryBaseProgramsSelectListener.onCategoryProgramsFavouriteClick(
//                            program.id ?: "",
//                            holder.bindingAdapterPosition
//                        )
                    lottieFavourite.playAnimation()
                } else {
                    lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                    lottieFavourite.progress = 1f
                    data.isFavorite = true
//                        categoryBaseProgramsSelectListener.onCategoryProgramsFavouriteClick(
//                            program.id ?: "",
//                            holder.bindingAdapterPosition
//                        )
                    lottieFavourite.playAnimation()
                }
            }
            /* ivLike.setOnClickListener {
                 if (ivLike.isVisible) {
                     ivLike.isGone = true
                     ivDisLike.isVisible = true
                 }
             }
             ivDisLike.setOnClickListener {
                 if (ivDisLike.isVisible) {
                     ivDisLike.isGone = true
                     ivLike.isVisible = true
                 }
             }*/
        }
    }

    inner class ItemViewHolder(var binding: ItemsCommunityRoomsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                mListener.onCommunityRoomsAdapterClick(bindingAdapterPosition)
            }
        }
    }

    interface OnCommunityRoomsItemClickListener {
        fun onCommunityRoomsAdapterClick(position: Int)
    }
}