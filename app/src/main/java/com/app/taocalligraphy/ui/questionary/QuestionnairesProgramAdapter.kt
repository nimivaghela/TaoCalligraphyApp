package com.app.taocalligraphy.ui.questionary

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
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class QuestionnairesProgramAdapter(
    private val multiSessionListener: OnMultiSessionListener,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val loadItem = 2
    private val multiItemItem = 1

    var list: ArrayList<ProgramDataModel?> = arrayListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
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
        } else
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
                    tvTitle.setTextColor(ContextCompat.getColor(tvTitle.context, R.color.gold))

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
                            multiSessionListener.onFavouriteClicked(data)
                            lottieFavourite.playAnimation()
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                            data?.isFavorites = true
                            multiSessionListener.onFavouriteClicked(data!!)
                            lottieFavourite.playAnimation()
                        }
                    }

                    ivFeaturedBg.gone()
                    ivFeaturedImage.gone()

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

    class LoadDataViewHolder(view: ItemLoadMoreHorizontalProgressBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface OnMultiSessionListener {
        fun onMultiSessionClicked(dataModel: ProgramDataModel)
        fun onFavouriteClicked(dataModel: ProgramDataModel)
    }

    class MultiSessionViewHolder(view: ItemMultiSessionListBinding) :
        RecyclerView.ViewHolder(view.root) {
        val item = view
    }
}