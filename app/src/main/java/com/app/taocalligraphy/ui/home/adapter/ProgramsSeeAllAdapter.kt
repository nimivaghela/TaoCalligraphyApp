package com.app.taocalligraphy.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreVerticalProgressBinding
import com.app.taocalligraphy.databinding.ItemProgramSeeAllBinding
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.set0Padding
import com.app.taocalligraphy.utils.extensions.set10Padding
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.getHtmlString
import com.app.taocalligraphy.utils.loadImage

class ProgramsSeeAllAdapter(var onMeditationItemClickListener: OnProgramItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<ProgramDataModel?> = arrayListOf()

    fun setProgramData(dataList: ArrayList<ProgramDataModel?>) {
        list = dataList
        notifyDataSetChanged()
    }

    private val ITEM_LOAD = 0
    private val ITEM_DATA = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ITEM_LOAD) {
            val itemBindingUtil =
                ItemLoadMoreVerticalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ProgressViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemProgramSeeAllBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            DataHolder(itemBindingUtil)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position] == null) {
            ITEM_LOAD
        } else {
            ITEM_DATA
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProgressViewHolder) {
//            holder.itemView.updateLayoutParams {
//                height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
//            }
        } else if (holder is DataHolder) {
            holder.binding.apply {
                list[position]?.let { data ->
                    ivProgram.loadImage(
                        data.thumbnailImage, R.drawable.img_default_for_content,
                        true
                    )
                    tvTitle.text = data.title
                    getHtmlString(tvDescription, data.description)

                    data.isFavorites.let {
                        if (data.isFavorites) {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                        }
                    }

                    ffFavourite.setOnClickListener {
                        if (data.isFavorites) {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                            data.isFavorites = false
                            onMeditationItemClickListener.onFavouriteClicked(data)
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            data.isFavorites = true
                            onMeditationItemClickListener.onFavouriteClicked(data!!)
                            lottieFavourite.playAnimation()
                        }
                    }

                    if (data.isFeatured == true) {
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

                    if ((data?.subscription?.isAccessible
                            ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false
                    ) {
//                    GET
                        tVGet.visible()
                        ivSubscribeLock.gone()
                        rlLock.gone()
                        ffFavourite.gone()
                    } else if ((data?.subscription?.isAccessible ?: true) == false) {
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
                        if (UserHolder.EnumUserModulePermission.ADD_FAVOURITE.permission?.canAccess
                                ?: false
                        ) {
                            ffFavourite.visible()
                        } else {
                            ffFavourite.gone()
                        }
                    }
                }
                root.setOnClickListener {
                    onMeditationItemClickListener.onProgramItemClicked(list[holder.bindingAdapterPosition]!!)
                }

            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DataHolder(var binding: ItemProgramSeeAllBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ProgressViewHolder(var binding: ItemLoadMoreVerticalProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface OnProgramItemClickListener {
        fun onProgramItemClicked(data: ProgramDataModel)
        fun onFavouriteClicked(dataModel: ProgramDataModel)
    }
}