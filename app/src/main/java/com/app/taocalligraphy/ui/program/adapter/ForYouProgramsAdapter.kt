package com.app.taocalligraphy.ui.program.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreHorizontalProgressBinding
import com.app.taocalligraphy.databinding.ItemMultiSessionListBinding
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class ForYouProgramsAdapter(
    private val mListener: ForYouProgramsSelectListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var programsList = ArrayList<ProgramDataModel?>()

    @SuppressLint("NotifyDataSetChanged")
    fun setForYouPrograms(forYouProgramsList: ArrayList<ProgramDataModel?>) {
        programsList = forYouProgramsList
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
        return if (programsList[position] == null) {
            Constants.ITEM_PROGRESS
        } else {
            Constants.ITEM_DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is DataViewHolder) {
            val data = programsList[holder.bindingAdapterPosition]
            data?.let {
                holder.item.apply {
                    llRating.visible()
                    ivProgram.loadImage(
                        data.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )

                    if (data.isFeatured == true) {
                        ivFeaturedBg.visible()
                        ivFeaturedImage.visible()
                        ivBackground.setImageResource(R.drawable.bg_rounded_right_corner_border_gold)
                    } else {
                        ivFeaturedBg.gone()
                        ivFeaturedImage.gone()
                        ivBackground.setImageResource(R.drawable.bg_rounded_right_corner_border)
                    }

                    tvTitle.text = data.title
                    tvTitle.setTextColor(ContextCompat.getColor(tvTitle.context, R.color.gold))
                    rbRating.rating = data.averageRatingCount?.toFloat() ?: 0.0f
                    tvRateCount.text = data.ratingCount
                    tvJoinedUser.text = data.joinedUserCount

                    if (data.isFavorites) {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                    }

                    flFavourite.setOnClickListener {
                        if (programsList[holder.bindingAdapterPosition]?.isFavorites == true) {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f

                            programsList[holder.bindingAdapterPosition]?.isFavorites = false

                            mListener.onForYouProgramsFavouriteClick(
                                programsList[holder.bindingAdapterPosition]?.id.toString(),
                                holder.bindingAdapterPosition
                            )
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            programsList[holder.bindingAdapterPosition]?.isFavorites = true

                            mListener.onForYouProgramsFavouriteClick(
                                programsList[holder.bindingAdapterPosition]?.id.toString(),
                                holder.bindingAdapterPosition
                            )
                            lottieFavourite.playAnimation()
                        }
                    }

                    llMain.setOnClickListener {
                        mListener.onForYouProgramsItemClick(programsList[holder.bindingAdapterPosition])
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
                }
            }
        } else if (holder is ProgressViewHolder) {
            holder.itemView.updateLayoutParams {
                height = if (isTablet(holder.itemView.context))
                    holder.itemView.context.resources.getDimension(R.dimen._60sdp).toInt()
                else
                    holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
            }
        }
    }

    override fun getItemCount(): Int {
        return programsList.size
    }

    class DataViewHolder(view: ItemMultiSessionListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ProgressViewHolder(view: ItemLoadMoreHorizontalProgressBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface ForYouProgramsSelectListener {
        fun onForYouProgramsFavouriteClick(id: String, adapterPosition: Int)
        fun onForYouProgramsItemClick(data: ProgramDataModel?)
    }
}