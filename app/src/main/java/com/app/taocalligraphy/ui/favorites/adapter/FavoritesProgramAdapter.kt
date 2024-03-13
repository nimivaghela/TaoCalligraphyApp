package com.app.taocalligraphy.ui.favorites.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreHorizontalProgressBinding
import com.app.taocalligraphy.databinding.ItemMultiSessionListBinding
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentDataModel
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class FavoritesProgramAdapter(
    var onProgramClicked: ProgramClicked
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<ProgramDataModel?> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setFavoritesProgramData(dataList: ArrayList<ProgramDataModel?>) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.ITEM_PROGRESS) {
            val itemBindingUtil =
                ItemLoadMoreHorizontalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ProgressViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemMultiSessionListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DataViewHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            Constants.ITEM_PROGRESS
        } else {
            Constants.ITEM_DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) {
            val data = list[position]
            holder.binding.apply {
                llRating.visible()
                tvTitle.text = data?.title
                tvTitle.setTextColor(ContextCompat.getColor(tvTitle.context, R.color.gold))
                ivProgram.loadImage(data?.thumbnailImage, R.drawable.img_default_for_content, true)
                tvJoinedUser.text = data?.joinedUserCount

                rbRating.rating = data?.averageRatingCount?.toFloat() ?: 0f
                tvRateCount.text = data?.ratingCount ?: "0"

                data?.isFavorites.let {
                    if (data?.isFavorites == true) {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
//                    lottieFavourite.playAnimation()
                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
//                    lottieFavourite.playAnimation()
                    }
                }
                flFavourite.setOnClickListener {
                    if (data?.isFavorites == true) {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                        data.isFavorites = false
                        onProgramClicked.onProgramFavoriteClicked(
                            holder.bindingAdapterPosition
                        )

                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
                        data?.isFavorites = true
                        onProgramClicked.onProgramFavoriteClicked(
                            holder.bindingAdapterPosition
                        )
                    }
                    lottieFavourite.playAnimation()
                }

                if ((data?.subscription?.isAccessible
                        ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false
                ) {
//                    GET
                    tVGet.visible()
                    ivSubscribeLock.gone()
                    rlLock.gone()
                    flFavourite.gone()
                } else if ((data?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
                    tVGet.gone()
                    ivSubscribeLock.visible()
                    rlLock.gone()
                    flFavourite.gone()
                } else {
//                    can access
                    tVGet.gone()
                    ivSubscribeLock.gone()
                    rlLock.gone()
                    flFavourite.visible()
                    if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess
                            ?: false
                    ) {
                        flFavourite.visible()
                    } else {
                        flFavourite.gone()
                    }
                }
                holder.itemView.setOnClickListener {
                    onProgramClicked.onProgramClicked(holder.bindingAdapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DataViewHolder(view: ItemMultiSessionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val binding = view
    }

    class ProgressViewHolder(view: ItemLoadMoreHorizontalProgressBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ProgramClicked {
        fun onProgramClicked(position: Int)
        fun onProgramFavoriteClicked(position: Int)
    }

}