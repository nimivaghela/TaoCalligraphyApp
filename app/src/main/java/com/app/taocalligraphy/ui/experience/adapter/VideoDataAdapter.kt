package com.app.taocalligraphy.ui.experience.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreHorizontalProgressBinding
import com.app.taocalligraphy.databinding.ItemVideoListBinding
import com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter.ContentData
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.isTablet
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class VideoDataAdapter(
    private val mListener: OnVideoItemClickListener,
    private val showPlayIcon: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val singleItem = 0
    private val loadItem = 1

    private var list: ArrayList<ContentData?> = ArrayList()

    @SuppressLint("NotifyDataSetChanged")
    fun setVideoData(dataList: ArrayList<ContentData?>) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == singleItem) {
            val itemBindingUtil =
                ItemVideoListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            AudioVideoDataAdapterViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemLoadMoreHorizontalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            LoadDataViewHolder(itemBindingUtil)
        }
    }

    override fun getItemCount(): Int = list.size
    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null)
            loadItem
        else
            singleItem

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AudioVideoDataAdapterViewHolder) {
            val data: ContentData? = list[holder.bindingAdapterPosition]
            holder.binding.apply {
                if (showPlayIcon) {
                    ivPlay.isVisible = true
                } else {
                    ivPlay.isGone = true
                }
                if (data?.isFavorite == true) {
                    lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                    lottieFavourite.progress = 1f
                } else {
                    lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                    lottieFavourite.progress = 1f
                }
                ffFavourite.setOnClickListener {
                    if (list[holder.bindingAdapterPosition]?.isFavorite == true) {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f

                        list[holder.bindingAdapterPosition]?.isFavorite = false

                        mListener.onVideoAdapterFavoriteClick(
                            list[holder.bindingAdapterPosition]?.id.toString(),
                            holder.bindingAdapterPosition
                        )
                        lottieFavourite.playAnimation()
                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
                        list[holder.bindingAdapterPosition]?.isFavorite = true

                        mListener.onVideoAdapterFavoriteClick(
                            list[holder.bindingAdapterPosition]?.id.toString(),
                            holder.bindingAdapterPosition
                        )
                        lottieFavourite.playAnimation()
                    }
                }

                with(list[position]) {
                    tvAudioVideoName.text = this?.contentName ?: ""
                    ivThumbImage.loadImage(
                        this?.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )
                }

                if ((data?.subscription?.isAccessible != false) == true && data?.isPaidContent == true && data.isPurchased == false
                ) {
//                    GET
                    tVGet.visible()
                    ivSubscribeLock.gone()
                    rlLock.gone()
                    ffFavourite.gone()
                } else if ((data?.subscription?.isAccessible != false) == false) {
//                        Subscribe
                    tVGet.gone()
                    ivSubscribeLock.visible()
                    rlLock.gone()
                    ffFavourite.gone()
                } else {
//                    can access
                    tVGet.gone()
                    ivSubscribeLock.gone()
                    rlLock.gone()
                    ffFavourite.visible()
                    if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess == true
                    ) {
                        ffFavourite.visible()
                    } else {
                        ffFavourite.gone()
                    }
                }



                if (data?.isFeatured == true) {
                    ivFeaturedBg.visible()
                    ivFeaturedImage.visible()
                } else {
                    ivFeaturedBg.gone()
                    ivFeaturedImage.gone()
                }
                root.setOnClickListener {
                    list[holder.bindingAdapterPosition]?.let { it1 ->
                        mListener.onVideoAdapterClick(
                            it1
                        )
                    }
                }
            }
        } else {
            holder.itemView.updateLayoutParams {
                height = if (isTablet(holder.itemView.context))
                    holder.itemView.context.resources.getDimension(R.dimen._50sdp).toInt()
                else
                    holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
            }
        }
    }

    inner class AudioVideoDataAdapterViewHolder(var binding: ItemVideoListBinding) :
        RecyclerView.ViewHolder(binding.root)

    class LoadDataViewHolder(var binding: ItemLoadMoreHorizontalProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnVideoItemClickListener {
        fun onVideoAdapterClick(data: ContentData?)
        fun onVideoAdapterFavoriteClick(id: String, adapterPosition: Int)
    }
}