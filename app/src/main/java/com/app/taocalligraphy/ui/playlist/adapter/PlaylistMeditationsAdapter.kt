package com.app.taocalligraphy.ui.playlist.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.app.taocalligraphy.R
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.utils.extensions.getContentCategoryTitleFromList
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeAdapter
import kotlinx.android.synthetic.main.item_playlist_meditations_list.view.*
import java.text.SimpleDateFormat
import java.util.*

class PlaylistMeditationsAdapter(
    private var context: Context,
    arrayList: MutableList<PlaylistContentFilterApiResponse.ContentList>
) : DragDropSwipeAdapter<PlaylistContentFilterApiResponse.ContentList, PlaylistMeditationsAdapter.ViewHolder>(
    arrayList
) {

    lateinit var onSelectList: OnSelect

    override fun getViewHolder(itemView: View): ViewHolder {
        return ViewHolder(itemView)
    }

    override fun getViewToTouchToStartDraggingItem(
        item: PlaylistContentFilterApiResponse.ContentList,
        viewHolder: ViewHolder,
        position: Int
    ): View? {
        return viewHolder.dragIcon
    }

    override fun canBeDragged(
        item: PlaylistContentFilterApiResponse.ContentList,
        viewHolder: ViewHolder,
        position: Int
    ): Boolean {
        return !dataSet[position].isForAddItem
    }

    override fun canBeDroppedOver(
        item: PlaylistContentFilterApiResponse.ContentList,
        viewHolder: ViewHolder,
        position: Int
    ): Boolean {
        return !dataSet[position].isForAddItem
    }

    override fun onBindViewHolder(
        contentItem: PlaylistContentFilterApiResponse.ContentList,
        viewHolder: ViewHolder,
        position: Int
    ) {

        // Hide divider if there is only one item (Add New Item)
        if (dataSet.size <= 1) {
            if (viewHolder.adapterPosition == 0) {
                viewHolder.itemView.divider.gone()
            } else {
                viewHolder.itemView.divider.visible()
            }
        } else {
            viewHolder.itemView.divider.visible()
        }

        var data = dataSet[position]

        // for Content
        if ( (data?.subscription?.isAccessible ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false) {
//                    GET
            viewHolder.itemView.tVGet.visible()
            viewHolder.itemView.ivSubscribeLock.gone()
        } else if ((data?.subscription?.isAccessible ?: true) == false) {
//                        Subscribe
            viewHolder.itemView.tVGet.gone()
            viewHolder.itemView.ivSubscribeLock.visible()
        } else {
//                    can access
            viewHolder.itemView.tVGet.gone()
            viewHolder.itemView.ivSubscribeLock.gone()
        }


        if(data.isForAddItem){
            viewHolder.itemView.tvContentTitle.gone()
            viewHolder.itemView.tvContentDuratiom.gone()
            viewHolder.itemView.tvContentCategory.gone()
            viewHolder.itemView.tvNewItem.visible()
            viewHolder.itemView.imgContent.setBackgroundResource(R.color.shimmer_light)
            viewHolder.itemView.imgContent.loadImage(
                "", R.color.shimmer_light, true
            )

            viewHolder.itemView.ivCenterImage.setBackgroundResource(R.drawable.bg_gold_round)
            viewHolder.itemView.ivCenterImage.setImageResource(R.drawable.vd_add_icon_white)
            viewHolder.itemView.ivCenterImage.setPadding(15,15,15,15)
            viewHolder.itemView.setOnClickListener{
                onSelectList.clickOnItem(data, position)
            }
            viewHolder.itemView.layout_swipe.gone()
            viewHolder.itemView.divider.gone()
        }else {
            viewHolder.itemView.tvContentTitle.visible()
            viewHolder.itemView.tvContentDuratiom.visible()
            viewHolder.itemView.tvContentCategory.visible()
            viewHolder.itemView.tvNewItem.gone()
            viewHolder.itemView.layout_swipe.visible()
            viewHolder.itemView.imgContent.loadImage(
                data.thumbnailImage, R.drawable.img_default_for_content, true
            )
            viewHolder.itemView.tvContentTitle.text = data.contentName
            viewHolder.itemView.ivCenterImage.setBackgroundResource(R.drawable.vd_play_button)
            viewHolder.itemView.ivCenterImage.setImageResource(R.color.transparent)
            try {
                viewHolder.itemView.tvContentCategory.text =
                    data.categoryDetailsList.getContentCategoryTitleFromList()
                val contentTimeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val contentDurationDate = contentTimeFormat.parse(data.contentDuration)
                viewHolder.itemView.tvContentDuratiom.text = "${getTotalTime(contentDurationDate)}"
            } catch (e: Exception) {
            }
            data.contentOrder = position + 1
            viewHolder.itemView.setOnClickListener{}
            viewHolder.itemView.imgRemove.setOnClickListener {
                viewHolder.itemView.swipeLayout.reset()
                onSelectList.clickOnItem(data, position)
            }
        }
    }

    override fun canBeSwiped(
        arrayList: PlaylistContentFilterApiResponse.ContentList,
        holder: ViewHolder,
        position: Int
    ): Boolean {
        return false
    }

    class ViewHolder(view: View) : DragDropSwipeAdapter.ViewHolder(view) {
        val dragIcon: ImageView = view.findViewById(R.id.ivDrag)
        val llMain: LinearLayout = view.findViewById(R.id.llMain)
    }

    interface OnSelect {
        fun clickOnItem(
            contentList: PlaylistContentFilterApiResponse.ContentList,
            position: Int
        )
    }

    fun setOnClickListener(onSelectList: OnSelect) {
        this.onSelectList = onSelectList
    }

    fun getTotalTime(date: Date): String {
        return "${
            if (date.hours > 0) {
                if (date.hours > 1) {
                    "${date.hours} "+context.getString(R.string.hrs_small)
                } else {
                    "${date.hours} "+context.getString(R.string.hr_smal)
                }
            } else {
                ""
            }
        }${
            if (date.minutes > 0) {
                if (date.minutes > 1) {
                    "${date.minutes} "+context.getString(R.string.mins)
                } else {
                    "${date.minutes} "+context.getString(R.string.min)
                }
            } else {
                ""
            }
        }${
            if (date.seconds > 0) {
                if (date.seconds > 1) {
                    "${date.seconds} "+context.getString(R.string.secs)
                } else {
                    "${date.seconds} "+context.getString(R.string.sec_small)
                }
            } else {
                ""
            }
        }"
    }

}