package com.app.taocalligraphy.ui.playlist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemPlaylistViewListBinding
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import com.app.taocalligraphy.utils.extensions.calculateDuration
import com.app.taocalligraphy.utils.extensions.getContentCategoryTitleFromList
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_playlist_view_list.view.*
import kotlinx.android.synthetic.main.item_playlist_view_list.view.tVGet

class PlaylistViewItemAdapter(var mDataList: MutableList<PlaylistContentFilterApiResponse.ContentList>) :
    RecyclerView.Adapter<PlaylistViewItemAdapter.ViewHolder>() {

    lateinit var onSelectList: OnSelect

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemPlaylistViewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.imgContent.loadImage(
            mDataList[position].thumbnailImage,
            R.drawable.img_default_for_content,
            true
        )
        holder.itemView.tvContentTitle.text = mDataList[position].contentName
        try {
            holder.itemView.tvContentCategory.text =
                mDataList[position].categoryDetailsList.getContentCategoryTitleFromList()
            holder.itemView.tvContentDuratiom.text = mDataList[position].contentDuration.calculateDuration()
        } catch (e: Exception) {
        }
        var data = mDataList[position]
        // for Content
        if ( (data?.subscription?.isAccessible ?: true) == true && data?.isPaidContent == true && data?.isPurchased == false) {
//          GET
            holder.itemView.tVGet.visible()
            holder.itemView.ivSubscribeLock.gone()
        } else if ((data?.subscription?.isAccessible ?: true) == false) {
//          Subscribe
            holder.itemView.tVGet.gone()
            holder.itemView.ivSubscribeLock.visible()
        } else {
//          can access
            holder.itemView.tVGet.gone()
            holder.itemView.ivSubscribeLock.gone()
        }


        mDataList[position].contentOrder = position + 1
        holder.itemView.setOnClickListener {
            onSelectList.clickOnItem(mDataList, holder.bindingAdapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(view: ItemPlaylistViewListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    fun updateList(arrayList: MutableList<PlaylistContentFilterApiResponse.ContentList>) {
        mDataList.clear()
        mDataList.addAll(arrayList)
        notifyDataSetChanged()
    }

    interface OnSelect {
        fun clickOnItem(
            cardList: MutableList<PlaylistContentFilterApiResponse.ContentList>,
            position: Int
        )
    }

    fun setOnClickListener(onSelectList: OnSelect) {
        this.onSelectList = onSelectList
    }
}