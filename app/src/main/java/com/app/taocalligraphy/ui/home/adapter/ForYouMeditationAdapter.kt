package com.app.taocalligraphy.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreHorizontalProgressBinding
import com.app.taocalligraphy.databinding.ItemMultiSessionListBinding
import com.app.taocalligraphy.databinding.ItemSingleSessionListBinding
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class ForYouMeditationAdapter(
    private val multiSessionListener: OnMultiSessionListener,
    private val isApplyThemeColor: Boolean = false,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val singleItem = 0
    private val loadItem = 2
    private val multiItemItem = 1

    private var list: ArrayList<ProgramDataModel?> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setMeditationData(dataList: ArrayList<ProgramDataModel?>) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            singleItem -> {
                val itemBindingUtil =
                    ItemSingleSessionListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return SingleSessionViewHolder(itemBindingUtil)
            }
            multiItemItem -> {
                val itemBindingUtil =
                    ItemMultiSessionListBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                return MultiSessionViewHolder(itemBindingUtil)
            }
            else -> {
                val itemBindingUtil =
                    ItemLoadMoreHorizontalProgressBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LoadDataViewHolder(itemBindingUtil)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            loadItem
        } else if (list[position]!!.type == Constants.content)
            singleItem
        else
            multiItemItem

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data: ProgramDataModel? = list[holder.bindingAdapterPosition]
        when (holder) {
            is MultiSessionViewHolder -> {
                holder.item.apply {
                    ivProgram.loadImage(
                        data?.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )

                    tvTitle.text = data?.title
                    if (isApplyThemeColor) {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.gold
                            )
                        )
                    } else {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.secondary_black
                            )
                        )
                    }

                    data?.isFavorites?.let {
                        if (data.isFavorites) {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                        }
                    } ?: kotlin.run {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                    }

                    flFavourite.setOnClickListener {
                        if (data?.isFavorites == true) {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                            data.isFavorites = false
                            multiSessionListener.onFavouriteClicked(data, true)
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            data?.isFavorites = true
                            multiSessionListener.onFavouriteClicked(data!!, true)
                            lottieFavourite.playAnimation()
                        }
                    }

                    if (data?.isFeatured == true) {
                        ivFeaturedBg.visible()
                        ivFeaturedImage.visible()
                        ivBackground.setImageResource(R.drawable.bg_rounded_right_corner_border_gold)
                    } else {
                        ivFeaturedBg.gone()
                        ivFeaturedImage.gone()
                        ivBackground.setImageResource(R.drawable.bg_rounded_right_corner_border)
                    }

                    // for Content
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
            is SingleSessionViewHolder -> {
                holder.item.apply {
                    ivProgram.loadImage(
                        data?.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )

                    tvTitle.text = data?.title
                    if (isApplyThemeColor) {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.gold
                            )
                        )
                    } else {
                        tvTitle.setTextColor(
                            ContextCompat.getColor(
                                holder.itemView.context,
                                R.color.secondary_black
                            )
                        )
                    }

                    if (data?.isFeatured == true) {
                        /* set10Padding(ivProgram)
                         set10Padding(ivContentBgGradient)*/
                        ivFeaturedBg.visible()
                        ivFeaturedImage.visible()
                    } else {
                        //set0Padding(ivProgram)
                        //set0Padding(ivContentBgGradient)
                        ivFeaturedBg.gone()
                        ivFeaturedImage.gone()
                    }

                    data?.isFavorites?.let {
                        if (data.isFavorites) {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                        }
                    } ?: kotlin.run {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                    }

                    flFavourite.setOnClickListener {
                        if (data?.isFavorites == true) {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                            data.isFavorites = false
                            multiSessionListener.onFavouriteClicked(data, true)
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            data?.isFavorites = true
                            multiSessionListener.onFavouriteClicked(data!!, true)
                            lottieFavourite.playAnimation()
                        }
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
            is LoadDataViewHolder -> {
                holder.itemView.updateLayoutParams {
                    height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
                }
            }
        }

        holder.itemView.setOnClickListener {
            data?.let { it1 ->
                multiSessionListener.onMultiSessionClicked(it1)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SingleSessionViewHolder(view: ItemSingleSessionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }


    class LoadDataViewHolder(view: ItemLoadMoreHorizontalProgressBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnMultiSessionListener {
        fun onMultiSessionClicked(dataModel: ProgramDataModel)
        fun onFavouriteClicked(dataModel: ProgramDataModel, isForYou: Boolean)
    }

    class MultiSessionViewHolder(view: ItemMultiSessionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

/* subscribe code old
    if ( (data?.subscription?.isAccessible ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false) {
//                    GET
        ivPlay.gone()
        tVGet.visible()
        tvSubscribe.gone()
        rlLock.gone()
//                    } else if ((data?.subscription?.isAccessible ?: true) == false && data?.unlockDays!! >= 1) {
////                    Unlock within `dynamic` days
//                        tVGet.gone()
//                        ivPlay.visible()
//                        tvSubscribe.visible()
//                        tvSubscribe.text =
//                            holder.itemView.context.getString(R.string.unlock_within) +
//                                    " " + data.unlockDays + " " + holder.itemView.context.getString(
//                                R.string.days
//                            )
//                        rlLock.gone()
//                    } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays != null) {
////                    lock
//                        tVGet.gone()
//                        ivPlay.visible()
//                        tvSubscribe.gone()
//                        rlLock.visible()
    } else if ((data?.subscription?.isAccessible ?: true) == false  && (data?.isPaidContent ?: false) == false) {
//                        Subscribe
        tVGet.gone()
        ivPlay.gone()
        tvSubscribe.visible()
        tvSubscribe.text = holder.itemView.context.getString(R.string.subscribe)
        rlLock.gone()
    } else {
//                    can access
        tVGet.gone()
        ivPlay.visible()
        tvSubscribe.gone()
        rlLock.gone()
    }
}*/
/* Subscribe Programs Old code
  if ( (data?.subscription?.isAccessible ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false) {
//                    GET
        ivPlay.gone()
        tVGet.visible()
        tvSubscribe.gone()
        rlLock.gone()
//                    } else if (data?.isLocked == true && data.isSubscribed == false) {
//                        if (data.unlockDays != null) {
//                            if (data.unlockDays!! >= 1) {
////                    Unlock within `dynamic` days
//                                tVGet.gone()
//                                ivPlay.visible()
//                                tvSubscribe.visible()
//                                tvSubscribe.text =
//                                    holder.itemView.context.getString(R.string.unlock_within) +
//                                            " " + data.unlockDays + " " + holder.itemView.context.getString(
//                                        R.string.days
//                                    )
//                                rlLock.gone()
//                            } else {
////                    lock
//                                tVGet.gone()
//                                ivPlay.visible()
//                                tvSubscribe.gone()
//                                rlLock.visible()
//                            }
//                        } else if (data.unlockDays == null) {
////                    lock
//                            tVGet.gone()
//                            ivPlay.visible()
//                            tvSubscribe.gone()
//                            rlLock.visible()
//                        }
    } else if ((data?.subscription?.isAccessible ?: true) == false && data?.isPaidContent == false) {
//                        Subscribe
        tVGet.gone()
        ivPlay.gone()
        tvSubscribe.visible()
        tvSubscribe.text = holder.itemView.context.getString(R.string.subscribe)
        rlLock.gone()
    } else {
//                    can access
        tVGet.gone()
        ivPlay.visible()
        tvSubscribe.gone()
        rlLock.gone()
    }

}*/
}