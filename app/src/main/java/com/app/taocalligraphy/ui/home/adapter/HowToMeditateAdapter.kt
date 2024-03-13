package com.app.taocalligraphy.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreHorizontalProgressBinding
import com.app.taocalligraphy.databinding.ItemSingleSessionListBinding
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class HowToMeditateAdapter(
    private val multiSessionListener: OnHowToMeditateClickListener,
    private val isApplyThemeColor: Boolean = false,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val singleItem = 0
    private val loadItem = 1

    private var list: ArrayList<HowToMeditateDataModel?> = arrayListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setMeditationData(dataList: ArrayList<HowToMeditateDataModel?>) {
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
        } else
            singleItem

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data: HowToMeditateDataModel? = list[holder.bindingAdapterPosition]
        when (holder) {
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


                    // for Content
                    if ( data?.isPaidContent == true && data?.isPurchased == false) {
//                    GET
                        tVGet.visible()
                        ivSubscribeLock.gone()
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
                }
                   /* if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == true && data.isPurchased == false) {
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
                    }*/

            }
            is LoadDataViewHolder -> {
                holder.itemView.updateLayoutParams {
                    height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
                }
            }
        }

        holder.itemView.setOnClickListener {
            data?.let { it1 ->
                multiSessionListener.onHowToMeditateClicked(it1)
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

    interface OnHowToMeditateClickListener {
        fun onHowToMeditateClicked(dataModel: HowToMeditateDataModel)
    }
}