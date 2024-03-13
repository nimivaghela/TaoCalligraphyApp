package com.app.taocalligraphy.ui.how_to_meditate.adapter

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemLoadMoreVerticalProgressBinding
import com.app.taocalligraphy.databinding.ItemWatchMeditateListBinding
import com.app.taocalligraphy.models.response.how_to_meditate_response.HowToMeditateDataModel
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImage

class WatchMeditateAdapter(
    private val watchMeditateClickListener: WatchMeditateClickListener,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list: ArrayList<HowToMeditateDataModel?> = arrayListOf()

    fun setWatchData(dataList: ArrayList<HowToMeditateDataModel?>) {
        list = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.ITEM_PROGRESS) {
            val itemBindingUtil =
                ItemLoadMoreVerticalProgressBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            ProgressViewHolder(itemBindingUtil)
        } else {
            val itemBindingUtil =
                ItemWatchMeditateListBinding.inflate(
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
        val data: HowToMeditateDataModel? = list[holder.bindingAdapterPosition]
        if (holder is DataViewHolder) {
            holder.binding.apply {
                tvMeditateTitle.text = data?.title
                val image = if (!data?.thumbnailImage.isNullOrEmpty()) {
                    data?.thumbnailImage
                } else {
                    data?.contentImage
                }
                ivMeditate.loadImage(image, R.drawable.img_default_for_content, true)
                tvTime.text = data?.time?.getContentTime()

                val description: String = setHtmlTextToTextView(data?.description).toString()
                val parts: ArrayList<String> =
                    description.split(" ") as ArrayList<String> /* = java.util.ArrayList<kotlin.String> */

                val textOne = SpannableStringBuilder()
                val textTwo = SpannableStringBuilder()

                parts.forEachIndexed { index, data ->
                    if (index == 0 || index == 1) {
                        textOne.append("$data ")
                        textOne.setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            textOne.length,
                            R.style.TextViewJostBoldStyle
                        )

                    } else {
                        textTwo.append("$data ")
                        textTwo.setSpan(
                            StyleSpan(Typeface.NORMAL),
                            0,
                            textTwo.length,
                            R.style.TextViewJostRegularStyle
                        )
                    }
                }

                val textThree = SpannableStringBuilder()
                textThree.append(textOne)
                textThree.append(textTwo)

                tvDescription.text = description

                if (data?.isFeatured == true) {
                    /*set10Padding(ivMeditate)
                    set10Padding(ivContentBgGradient)*/

                    ivFeaturedBg.visible()
                    ivFeaturedImage.visible()

                } else {
                    /*set0Padding(ivMeditate)
                    set0Padding(ivContentBgGradient)*/
                    ivFeaturedBg.gone()
                    ivFeaturedImage.gone()

                }
                if (data?.isLocked == false && data.isSubscribed == false && data.isPaidContent == true) {
//                    GET
                    ivPlay.gone()
                    tVGet.visible()
                    tvSubscribe.gone()
                    rlLock.gone()
                } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays!! >= 1) {
//                    Unlock within `dynamic` days
                    tVGet.gone()
                    ivPlay.visible()
                    tvSubscribe.visible()
                    tvSubscribe.text = holder.itemView.context.getString(R.string.unlock_days, data.unlockDays.toString())
                    rlLock.gone()
                } else if (data?.isLocked == true && data.isSubscribed == false && data.unlockDays != null) {
//                    lock
                    tVGet.gone()
                    ivPlay.visible()
                    tvSubscribe.gone()
                    rlLock.visible()
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
            }

            if (holder.bindingAdapterPosition % 2 == 0) {
                holder.item.llMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            } else {
                holder.item.llMain.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.shimmer_light_50
                    )
                )
            }
        } else {
            holder.itemView.updateLayoutParams {
                height = holder.itemView.context.resources.getDimension(R.dimen._110sdp).toInt()
            }
        }

        holder.itemView.setOnClickListener {
            watchMeditateClickListener.onWatchMeditateClick(data)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class DataViewHolder(var binding: ItemWatchMeditateListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val item = binding
    }

    class ProgressViewHolder(var binding: ItemLoadMoreVerticalProgressBinding) :
        RecyclerView.ViewHolder(binding.root)

    interface WatchMeditateClickListener {
        fun onWatchMeditateClick(data: HowToMeditateDataModel?)
    }
}