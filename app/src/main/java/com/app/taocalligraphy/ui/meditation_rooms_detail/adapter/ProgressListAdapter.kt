package com.app.taocalligraphy.ui.meditation_rooms_detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemProgramsListBinding
import com.app.taocalligraphy.models.response.program.ProgramDataModel
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class ProgressListAdapter(private val categoryBaseProgramsSelectListener: CategoryBaseProgramsSelectListener) :
    RecyclerView.Adapter<ProgressListAdapter.ViewHolder>() {

    private var list = ArrayList<ProgramDataModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun setProgramsData(programList: ArrayList<ProgramDataModel>) {
        list = programList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemProgramsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.apply {
            if (holder.bindingAdapterPosition % 2 == 0) {
                llMain.setBackgroundColor(
                    ContextCompat.getColor(
                        llMain.context,
                        R.color.transparent
                    )
                )
            } else {
                llMain.setBackgroundColor(
                    ContextCompat.getColor(
                        llMain.context,
                        R.color.medium_grey_10
                    )
                )
            }

            val data = list[holder.bindingAdapterPosition]
            ivProgram.loadImage(
                data.thumbnailImage,
                R.drawable.img_default_for_content,
                true
            )
            tvProgramName.text = data.title
            rbRating.rating = data.averageRatingCount?.toFloat() ?: 0f
            tvRateCount.text = data.ratingCount
            tvUsersCount.text = data.joinedUserCount

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

//            if (data.isLocked == false && data.isSubscribed == false && data.isPaidContent == true) {
////                    GET
//                ivPlay.gone()
//                tVGet.visible()
//                tvSubscribe.gone()
//                rlLock.gone()
//            } else if (data.isLocked == true && data.isSubscribed == false && data.unlockDays!! >= 1) {
////                    Unlock within `dynamic` days
//                tVGet.gone()
//                ivPlay.visible()
//                tvSubscribe.visible()
//                tvSubscribe.text = holder.itemView.context.getString(R.string.unlock_days, data.unlockDays.toString())
//                rlLock.gone()
//            } else if (data.isLocked == true && data.isSubscribed == false && data.unlockDays != null) {
////                    lock
//                tVGet.gone()
//                ivPlay.visible()
//                tvSubscribe.gone()
//                rlLock.visible()
//            } else if (data.isLocked == false && data.isSubscribed == false && data.isPaidContent == false) {
////                        Subscribe
//                tVGet.gone()
//                ivPlay.gone()
//                tvSubscribe.visible()
//                tvSubscribe.text = holder.itemView.context.getString(R.string.subscribe)
//                rlLock.gone()
//            } else {
////                    can access
//                tVGet.gone()
//                ivPlay.visible()
//                tvSubscribe.gone()
//                rlLock.gone()
//            }
            if (data.isFavorites == true) {
                lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                lottieFavourite.progress = 1f
            } else {
                lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                lottieFavourite.progress = 1f
            }
            ffFavourite.setOnClickListener {
                if (data.isFavorites == true) {
                    lottieFavourite.progress = 1f
                    categoryBaseProgramsSelectListener.onCategoryProgramsFavouriteClick(
                        data.id ?: "",
                        holder.bindingAdapterPosition
                    )
                    lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                    lottieFavourite.playAnimation()
                    data.isFavorites = false
                } else {
                    lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                    lottieFavourite.progress = 1f
                    categoryBaseProgramsSelectListener.onCategoryProgramsFavouriteClick(
                        data.id ?: "",
                        holder.bindingAdapterPosition
                    )
                    lottieFavourite.playAnimation()
                    data.isFavorites = true
                }
            }

            llMain.setOnClickListener {
                if (list.isNotEmpty()) {
                    categoryBaseProgramsSelectListener.onCategoryProgramsItemClick(list[holder.bindingAdapterPosition])
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(var binding: ItemProgramsListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    interface CategoryBaseProgramsSelectListener {
        fun onCategoryProgramsFavouriteClick(id: String, adapterPosition: Int)
        fun onCategoryProgramsItemClick(data: ProgramDataModel?)
    }
}