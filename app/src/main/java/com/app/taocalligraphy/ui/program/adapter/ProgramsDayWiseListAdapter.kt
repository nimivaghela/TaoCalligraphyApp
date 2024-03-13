package com.app.taocalligraphy.ui.program.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemProgramsDayWiseListBinding
import com.app.taocalligraphy.models.response.program.UserProgramContentDetailApiRes
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_programs_day_wise_list.view.*

class ProgramsDayWiseListAdapter(
    private var context: Context,
    private var listener: ProgramClickListener
) : RecyclerView.Adapter<ProgramsDayWiseListAdapter.ViewHolder>() {

    private var mViewMore = context.getString(R.string.mViewMore)
    var mIsProgramJoined = false
    private var mDataList: ArrayList<UserProgramContentDetailApiRes.Data> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil = ItemProgramsDayWiseListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mDataList[position].apply {
            holder.item.tvProgramsDesc.text =
                fromHtml(contentDescription)
            holder.item.tvProgramsDesc.addListenerForMore(object : MoreClickListener {
                override fun onMoreClick(contentId: Int) {
                    if (isPlayEnable)
                        programContentId?.let { listener.onProgramDescMoreClick(contentId, it) }
                }
            }, contentId!!)
            holder.item.tvProgramsDesc.setShowingLine(3)
            holder.item.tvProgramsDesc.addShowMoreText(mViewMore)
            holder.item.tvProgramsDesc.setShowMoreColor(
                ContextCompat.getColor(
                    context,
                    R.color.gold
                )
            )
            holder.item.tvProgramsDesc.setShowLessTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.gold
                )
            )

            holder.item.ivBackgroundImage.loadImage(
                thumbnailImage,
                R.drawable.img_default_for_content,
                true
            )
            holder.item.tvProgramsName.text = contentTitle
            holder.item.ivPlay.visibility = if (isPlayed == true) GONE else VISIBLE
            holder.item.ivDone.visibility = if (isPlayed == true) VISIBLE else GONE

            holder.item.tvDuration.text = contentDuration?.calculateDuration()
            holder.item.tvTime.text =
                combinedDate.getTimeBasedOnTimeFormat(holder.item.tvTime.context)
            if (isPlayEnable) {
                holder.item.fmMain.isClickable = true
//                holder.item.ivWhiteBackground.visibility = GONE
                holder.item.fmMain.alpha = 1f
            } else {
                holder.item.fmMain.isClickable = false
//                holder.item.ivWhiteBackground.visibility = VISIBLE
                holder.item.fmMain.alpha = 0.5f
            }

            if (isPlayed == true) {
                holder.item.fmMain.isClickable = true
            }

            // for Content
            if ((this?.subscription?.isAccessible
                    ?: true) == true && this?.isPaidContent == true && this?.isPurchased == false
            ) {
//                    GET
                holder.itemView.tVGet.visible()
                holder.itemView.ivSubscribeLock.gone()
            } else if ((this?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
                holder.itemView.tVGet.gone()
                holder.itemView.ivSubscribeLock.visible()
            } else {
//                    can access
                holder.itemView.tVGet.gone()
                holder.itemView.ivSubscribeLock.gone()
            }

            holder.item.llMain.clickWithDebounce {
                if (isPlayEnable || isPlayed == true)
                    listener.onProgramPlayClick(contentId!!, programContentId!!)
            }
        }

        if (position >= 1 && position == 1) {
            val params = holder.item.fmMain.layoutParams as StaggeredGridLayoutManager.LayoutParams
            params.setMargins(0, 180, 0, 0)
            holder.item.fmMain.layoutParams = params
        }

        if (!mIsProgramJoined) {
            holder.item.fmMain.alpha = 1f
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(view: ItemProgramsDayWiseListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    @SuppressWarnings("deprecation")
    fun fromHtml(html: String?): Spanned {
        return when {
            html == null -> {
                SpannableString("")
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> {
                Html.fromHtml(html, Html.FROM_HTML_MODE_COMPACT)
            }
            else -> {
                Html.fromHtml(html)
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateList(
        dataList: ArrayList<UserProgramContentDetailApiRes.Data>,
        isProgramJoined: Boolean
    ) {
        this.mIsProgramJoined = isProgramJoined
        mDataList = dataList
        notifyDataSetChanged()
    }


    interface MoreClickListener {
        fun onMoreClick(contentId: Int)
    }

    interface ProgramClickListener {
        fun onProgramPlayClick(contentId: Int, programContentId: Int)
        fun onProgramDescMoreClick(contentId: Int, programContentId: Int)
    }
}