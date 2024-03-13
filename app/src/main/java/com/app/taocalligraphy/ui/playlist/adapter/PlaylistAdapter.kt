package com.app.taocalligraphy.ui.playlist.adapter

import android.content.Context
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.databinding.ItemPlaylistListBinding
import com.app.taocalligraphy.models.response.playList.PlaylistApiResponse
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.gone
import com.app.taocalligraphy.utils.extensions.visible
import com.app.taocalligraphy.utils.loadImage
import kotlinx.android.synthetic.main.item_playlist_list.view.*

class PlaylistAdapter(
    private var  context: Context,
    private val playlistItemClickListener: PlaylistItemClickListener,
    var mDataList: MutableList<PlaylistApiResponse.Playlist>,
) :
    RecyclerView.Adapter<PlaylistAdapter.ViewHolder>() {

    private var mLastClickTime: Long = 0


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemPlaylistListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder.bindingAdapterPosition == 0) {
            holder.item.ivImage.setImageResource(R.color.shimmer_light)
            holder.item.tvNewPlaylist.isVisible = true
            holder.item.llPlaylistInfo.isGone = true
            holder.item.ivCenterImage.isGone = true
            holder.item.ivAddNew.isVisible = true
            if(!(UserHolder.EnumUserModulePermission.CREATE_PLAYLIST.permission?.canAccess ?: false)) {
                holder.item.clMain.alpha = 0.5f
                holder.item.ivSubscribeLock.visible()
            } else{
                holder.item.clMain.alpha = 1f
                holder.item.ivSubscribeLock.gone()
            }
        } else {
            holder.item.ivImage.loadImage(
                mDataList[position].playlistImage,
                R.drawable.img_default_for_content,
                true
            )
            holder.item.tvTitle.text = mDataList[position].title
            holder.item.tvTotalMeditation.text = "${mDataList[position].meditations} Meditations"
            holder.item.tvHoursMinute.text = getTotalTime(mDataList[position].timeDurationInMinutes)

            holder.item.tvNewPlaylist.isGone = true
            holder.item.llPlaylistInfo.isVisible = true
            holder.item.ivCenterImage.isVisible = true
            holder.item.ivAddNew.isGone = true
        }

        if ((holder.bindingAdapterPosition) % 2 == 0) {
            holder.item.clMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
        } else {
            holder.item.clMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.medium_grey_10
                )
            )
        }

        holder.itemView.setOnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return@setOnClickListener
            }
            mLastClickTime = SystemClock.elapsedRealtime()
            playlistItemClickListener.onPlaylistItemClick(holder.adapterPosition, mDataList)
        }
        holder.itemView.ivAddNew.setOnClickListener {
            holder.itemView.performClick()
        }
    }

    override fun getItemCount(): Int {
        return mDataList.size
    }

    class ViewHolder(view: ItemPlaylistListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface PlaylistItemClickListener {
        fun onPlaylistItemClick(
            adapterPosition: Int,
            mDataList: MutableList<PlaylistApiResponse.Playlist>
        )
    }

    fun updateList(arrayList: ArrayList<PlaylistApiResponse.Playlist>) {
        mDataList.clear()
        mDataList.addAll(arrayList)
        notifyDataSetChanged()
    }

    private fun getTotalTime(totalMinutes: Int): String {
        val hours: Int = totalMinutes / 60 //since both are ints, you get an int
        val minutes: Int = totalMinutes % 60
        return "${
            if (hours > 0) {
                if (hours > 1) {
                    "$hours ${context.getString(R.string.hrs_small)} "
                } else {
                    "$hours ${context.getString(R.string.hr_smal)} "
                }
            } else {
                ""
            }
        }${
            if (minutes > 0) {
                if (minutes > 1) {
                    "$minutes "+context.getString(R.string.mins)
                } else {
                    "$minutes "+context.getString(R.string.min)
                }
            } else {
                ""
            }
        }"
    }

}