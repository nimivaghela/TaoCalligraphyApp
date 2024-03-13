package com.app.taocalligraphy.ui.favorites.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreHorizontalProgressBinding
import com.app.taocalligraphy.databinding.ItemMeditationSearchBinding
import com.app.taocalligraphy.models.response.favorite_models.FavoriteContentDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.getSecondsFromTime
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.getHtmlString
import com.app.taocalligraphy.utils.loadImage

class FavoriteMeditationsListAdapter(
    private var context: Context,
    private var onGetListener: OnGetListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var list: ArrayList<FavoriteContentDataModel?> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setFavoritesMeditationData(dataList: ArrayList<FavoriteContentDataModel?>) {
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
                ItemMeditationSearchBinding.inflate(
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
            holder.item.apply {
                val data: FavoriteContentDataModel? = list[position]
                tvTitle.text = data?.contentName
                if (data?.contentDuration != null) {
                    tvType.text = data.type + "/" + getSecondsFromTime(
                        data.contentDuration
                    )
                } else {
                    tvType.text = data?.type + "/" + context.getString(R.string.zero_min)
                }

                when (data?.type) {
                    Constants.MUSIC -> {
                        ivType.setImageResource(R.drawable.vd_music_grey_icon)
                        ivType.visible()
                    }
                    Constants.VIDEO -> {
                        ivType.setImageResource(R.drawable.vd_video_grey_icon)
                        ivType.visible()
                    }
                    Constants.TEXT -> {
                        ivType.gone()
                    }
                    Constants.Guided_Meditation_Audio -> {
                        ivType.visible()
                        ivType.setImageResource(R.drawable.vd_audio_grey_icon)
                        tvType.text = tvType.context.getString(
                            R.string.search_result_meditation_type_with_time,
                            Constants.GUIDED,
                            getSecondsFromTime(data.contentDuration)
                        )
                    }
                }

                data?.description?.let {
                    getHtmlString(tvDescription, data.description)
                }

                ivMeditationSearch.loadImage(
                    data?.thumbnailImage,
                    R.drawable.img_default_for_content,
                    true
                )

                if (data?.isFavorite == true) {
                    lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                    lottieFavourite.progress = 1f
//                    lottieFavourite.playAnimation()
                } else {
                    lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                    lottieFavourite.progress = 1f
//                    lottieFavourite.playAnimation()
                }

                if ( (data?.subscription?.isAccessible ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false) {
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

                /*if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == true && data.isPurchased == false) {
//                    GET
                    ivPlay.gone()
                    tVGet.visible()
                    tvSubscribe.gone()
                    rlLock.gone()
                } else if (data?.isLocked == true && data.isSubscribed == false) {
                    if (data.unlockDays != null) {
                        if (data.unlockDays!! >= 1) {
//                    Unlock within `dynamic` days
                            tVGet.gone()
                            ivPlay.visible()
                            tvSubscribe.visible()
                            tvSubscribe.text =
                                holder.itemView.context.getString(R.string.unlock_within) +
                                        " " + data.unlockDays + " " + holder.itemView.context.getString(
                                    R.string.days
                                )
                            rlLock.gone()
                        } else {
//                    lock
                            tVGet.gone()
                            ivPlay.visible()
                            tvSubscribe.gone()
                            rlLock.visible()
                        }
                    } else if (data.unlockDays == null) {
//                    lock
                        tVGet.gone()
                        ivPlay.visible()
                        tvSubscribe.gone()
                        rlLock.visible()
                    }
                } else if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == false) {
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
*/

                flFavourite.setOnClickListener {
                    if (data?.isFavorite == true) {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                        data.isFavorite = false
                        onGetListener.isFavoriteSelected(
                            holder.bindingAdapterPosition
                        )
                        lottieFavourite.playAnimation()
                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
                        data?.isFavorite = true
                        onGetListener.isFavoriteSelected(
                            holder.bindingAdapterPosition
                        )
                        lottieFavourite.playAnimation()
                    }
                }

                root.setOnClickListener {
                    onGetListener.onGetClicked(holder.bindingAdapterPosition)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }


    class DataViewHolder(view: ItemMeditationSearchBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    class ProgressViewHolder(view: ItemLoadMoreHorizontalProgressBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnGetListener {
        fun onGetClicked(position: Int)
        fun isFavoriteSelected(position: Int)
    }


}