package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemsFeaturedRoomsBinding
import com.app.taocalligraphy.models.dummy_models.RoomDummyDataModel

class FeaturedRoomsAdapter(
    private val mListener: OnFeaturedRoomsItemClickListener
) : RecyclerView.Adapter<FeaturedRoomsAdapter.ItemViewHolder>() {
    var list: ArrayList<RoomDummyDataModel> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.items_featured_rooms,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val data = list[position]

        holder.binding.apply {
            when (position) {
                0 -> {
                    fmLve.visibility = View.GONE
                }
                1 -> {
                    fmLve.visibility = View.GONE
                }
            }

            root.setOnClickListener {
                mListener.onFeaturedRoomsAdapterClick(holder.bindingAdapterPosition)
            }

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
//                    if (ivFav.isVisible) {
//                        ivFav.isGone = true
//                        ivUnFav.isVisible = true
//                    } else {
//                        ivFav.isVisible = true
//                        ivUnFav.isGone = true
//                    }
            }
        }
    }

    inner class ItemViewHolder(var binding: ItemsFeaturedRoomsBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    interface OnFeaturedRoomsItemClickListener {
        fun onFeaturedRoomsAdapterClick(position: Int)
    }
}