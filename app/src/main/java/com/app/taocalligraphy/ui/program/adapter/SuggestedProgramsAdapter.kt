package com.app.taocalligraphy.ui.program.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemSuggestedProgramBinding
import com.app.taocalligraphy.models.response.program.ForYouProgramListResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage

class SuggestedProgramsAdapter(
    private val mListener: SuggestedProgramsListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var programsList = ArrayList<ForYouProgramListResponse.ForYouProgramList.Program?>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemBindingUtil =
            ItemSuggestedProgramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(itemBindingUtil)
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

                    tvProgramName.text = data.title

                    if (data.isFavorite == true) {
                        lottieFavourite.setAnimation("favorite_toggle_on_white.json")
                        lottieFavourite.progress = 1f
                    } else {
                        lottieFavourite.setAnimation("favorite_toggle_off_white.json")
                        lottieFavourite.progress = 1f
                    }

                    /*  ffFavourite.setOnClickListener {
                          mListener.onForYouProgramsFavouriteClick(
                              data.id.toString(),
                              holder.bindingAdapterPosition
                          )
                      }*/

                    llMain.setOnClickListener {
                        mListener.onSuggestedProgramsItemClick(programsList[position])
                    }

                    if (data.isLocked == false && data.isSubscribed == false && data.isPaidContent == true && data.isPurchased == false) {
//                    GET
                        tVGet.visible()
                        ivSubscribeLock.gone()
                        rlLock.gone()
                    } else if (data.isLocked == true && data.isSubscribed == false && data.unlockDays != null) {
//                    Unlock within `dynamic` days
                        if (data.unlockDays!! >= 1) {
                            tVGet.gone()
                            ivSubscribeLock.visible()
                            rlLock.gone()
                        } else {
//                    lock
                            tVGet.gone()
                            ivPlay.visible()
                            ivSubscribeLock.gone()
                            rlLock.visible()
                        }
                    } else if (data.isLocked == true && data.isSubscribed == false && data.unlockDays == null) {
//                    lock
                        tVGet.gone()
                        ivSubscribeLock.gone()
                        rlLock.visible()
                    } else if (data.isLocked == false && data.isSubscribed == false && data.isPaidContent == false) {
//                        Subscribe
                        tVGet.gone()
                        ivSubscribeLock.visible()

                        rlLock.gone()
                    } else {
//                    can access
                        tVGet.gone()
                        ivSubscribeLock.gone()
                        rlLock.gone()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return programsList.size
    }

    class DataViewHolder(view: ItemSuggestedProgramBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface SuggestedProgramsListener {
        fun onSuggestedProgramsItemClick(data: ForYouProgramListResponse.ForYouProgramList.Program?)
    }
}