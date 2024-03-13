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
import com.app.taocalligraphy.models.response.program.InProgressProgramListResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class InProgressProgramsAdapter(
    private val mListener: ProgressProgramsSelectListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var programsList = ArrayList<InProgressProgramListResponse.InProgressProgramsList.Program?>()

    @SuppressLint("NotifyDataSetChanged")
    fun setInProgressPrograms(programsList : ArrayList<InProgressProgramListResponse.InProgressProgramsList.Program?>) {
        this.programsList = programsList
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
                    programProgress.visible()
                    ivProgram.loadImage(
                        data.thumbnailImage,
                        R.drawable.img_default_for_content,
                        true
                    )

                    tvTitle.text = data.title
                    tvTitle.setTextColor(ContextCompat.getColor(tvTitle.context, R.color.gold))

                    val completedPercentage =
                        ((data.completedDays ?: 0) * 100) / (data.programTotalDays ?: 1)
                    programProgress.progress =
                        completedPercentage.toFloat()

                    if (data.isFavorite == true) {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                    }

                    if (data.isFeatured == true) {
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
                    flFavourite.setOnClickListener {
                        mListener.onProgressProgramsFavouriteClick(
                            data.id.toString(),
                            holder.bindingAdapterPosition
                        )

                        data.isFavorite = data.isFavorite != true

                        if (data.isFavorite == true) {
                            lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                            lottieFavourite.progress = 1f
                        } else {
                            lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                            lottieFavourite.progress = 1f
                        }
                        lottieFavourite.playAnimation()
                    }
                    llMain.setOnClickListener {
                        mListener.onProgressProgramsItemClick(programsList[holder.bindingAdapterPosition])
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
                height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
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

    interface ProgressProgramsSelectListener {
        fun onProgressProgramsItemClick(data: InProgressProgramListResponse.InProgressProgramsList.Program?)
        fun onProgressProgramsFavouriteClick(id: String, adapterPosition: Int)
    }
}