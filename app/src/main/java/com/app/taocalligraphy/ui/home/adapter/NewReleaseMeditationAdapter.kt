package com.app.taocalligraphy.ui.home.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
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
import com.app.taocalligraphy.utils.extensions.set0Padding
import com.app.taocalligraphy.utils.extensions.set10Padding
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class NewReleaseMeditationAdapter(
    private val onItemClicked: OnMultiSessionListener
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
                            onItemClicked.onFavouriteClicked(data, false)
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            data?.isFavorites = true
                            onItemClicked.onFavouriteClicked(data!!, false)
                            lottieFavourite.playAnimation()
                        }
                    }

                    if (data?.isFeatured == true) {
                        /*set10Padding(ivProgram)
                        set10Padding(ivContentBgGradient)*/

                        ivFeaturedBg.visible()
                        ivFeaturedImage.visible()
                        ivBackground.setImageResource(R.drawable.bg_rounded_right_corner_border_gold)
                    } else {
                        /*set0Padding(ivProgram)
                        set0Padding(ivContentBgGradient)*/

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
                        if(UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess ?: false){
                            flFavourite.visible()
                        }else{
                            flFavourite.gone()
                        }
                    }

//                    if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == true && data.isPurchased == false) {
////                    GET
//                        ivPlay.gone()
//                        tVGet.visible()
//                        tvSubscribe.gone()
//                        rlLock.gone()
//                    } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays!! >= 1) {
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
//                    } else if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == false) {
////                        Subscribe
//                        tVGet.gone()
//                        ivPlay.gone()
//                        tvSubscribe.visible()
//                        tvSubscribe.text = holder.itemView.context.getString(R.string.subscribe)
//                        rlLock.gone()
//                    } else {
////                    can access
//                        tVGet.gone()
//                        ivPlay.visible()
//                        tvSubscribe.gone()
//                        rlLock.gone()
//                    }
                }
            }
            is SingleSessionViewHolder -> {
                holder.item.apply {
                    if (data?.isFeatured == true) {
                        ivFeaturedBg.visible()
                        ivFeaturedImage.visible()
                    } else {
                        ivFeaturedBg.gone()
                        ivFeaturedImage.gone()
                    }
                    ivProgram.loadImage(
                        data?.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )
                    tvTitle.text = data?.title

                    if (data?.isFeatured == true) {
                        /*set10Padding(ivProgram)
                        set10Padding(ivContentBgGradient)*/

                        ivFeaturedBg.visible()
                        ivFeaturedImage.visible()
                    } else {
                        /*set0Padding(ivProgram)
                        set0Padding(ivContentBgGradient)*/

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
                            onItemClicked.onFavouriteClicked(data, false)
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            data?.isFavorites = true
                            onItemClicked.onFavouriteClicked(data!!, false)
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

                        if(UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess ?: false){
                            flFavourite.visible()
                        }else{
                            flFavourite.gone()
                        }
                    }


//                    if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == true) {
////                    GET
//                        ivPlay.gone()
//                        tVGet.visible()
//                        tvSubscribe.gone()
//                        rlLock.gone()
//                    } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays!! >= 1) {
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
//                    } else if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == false) {
////                        Subscribe
//                        tVGet.gone()
//                        ivPlay.gone()
//                        tvSubscribe.visible()
//                        tvSubscribe.text = holder.itemView.context.getString(R.string.subscribe)
//                        rlLock.gone()
//                    } else {
////                    can access
//                        tVGet.gone()
//                        ivPlay.visible()
//                        tvSubscribe.gone()
//                        rlLock.gone()
//                    }
                }
            }
            is LoadDataViewHolder -> {
                holder.itemView.updateLayoutParams {
                    height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
                }
            }
        }

        holder.itemView.setOnClickListener {
            onItemClicked.onMultiSessionClicked(data!!)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class SingleSessionViewHolder(view: ItemSingleSessionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class MultiSessionViewHolder(view: ItemMultiSessionListBinding) :
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
}