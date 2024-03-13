package com.app.taocalligraphy.ui.meditation_rooms_list.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemsOfficialRoomsBinding
import com.app.taocalligraphy.models.dummy_models.RoomDummyDataModel

class OfficialRoomsAdapter(
    private val mListener: OnOfficialRoomsItemClickListener
) : RecyclerView.Adapter<OfficialRoomsAdapter.ItemViewHolder>() {
    var list: ArrayList<RoomDummyDataModel> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.items_official_rooms,
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
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.red
                        )
                    )
                    fmLve.visibility = View.VISIBLE
                }
                1 -> {
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.shimmer_light
                        )
                    )
                    fmLve.visibility = View.GONE
                }
                2 -> {
                    cvBackground.setCardBackgroundColor(
                        ContextCompat.getColor(
                            cvBackground.context,
                            R.color.shimmer_light
                        )
                    )
                    fmLve.visibility = View.GONE
                }
            }
            ffFavourite.setOnClickListener {
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
            /*frameFav.setOnClickListener {

                if (ivFav.isVisible) {
                    ivFav.isGone = true
                    ivUnFav.isVisible = true
                } else {
                    ivFav.isVisible = true
                    ivUnFav.isGone = true
                }
            }*/
        }

    }

    inner class ItemViewHolder(var binding: ItemsOfficialRoomsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                mListener.onOfficialRoomsAdapterClick(bindingAdapterPosition)
            }
        }
    }

    interface OnOfficialRoomsItemClickListener {
        fun onOfficialRoomsAdapterClick(position: Int)
    }
}