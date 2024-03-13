package com.app.taocalligraphy.ui.downloads.adapter

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.databinding.ItemDownloadListBinding
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants
import com.app.taocalligraphy.ui.downloads.DownloadViewModel
import com.app.taocalligraphy.utils.extensions.*
import com.app.taocalligraphy.utils.loadImage
import com.google.android.exoplayer2.offline.Download

class DownloadsAdapter(private val downloadItemClickListener: DownloadItemClickListener, private val viewModel:DownloadViewModel) :
    RecyclerView.Adapter<DownloadsAdapter.ViewHolder>() {

    private var downloadList: List<MeditationContentResponse> = ArrayList()

    var handlerList = ArrayList<Handler>()
    var runnableList = ArrayList<Runnable>()

    fun setUserDownloads(downloadList: List<MeditationContentResponse>) {
        this.downloadList = downloadList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBindingUtil =
            ItemDownloadListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBindingUtil)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position % 2 == 0) {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    R.color.medium_grey_10
                )
            )
        } else {
            holder.item.llMain.setBackgroundColor(
                ContextCompat.getColor(
                    holder.itemView.context,
                    android.R.color.transparent
                )
            )
        }
        val download = downloadList[position]
        download.let {
            holder.item.circleProgressDownload.tag = it.id
            holder.item.ivContent.loadImage(
                if (it.type == Constants.VIDEO) it.backgroundImageMobile else it.thumbnailImage,
                R.drawable.img_default_for_content, true
            )
            holder.item.tvContentTitle.text = it.title
            holder.item.tvCategoryTitle.text =
                download.categoryDetailsList.getCategoryTitleFromList()
            holder.item.tvContentFileSize.text = it.contentFileSize.getContentFileSize()
        }
        if (viewModel.isEditEnable) {
            holder.item.ivSelect.visible()
            holder.item.ivDownloadComplete.gone()
            holder.item.swipeLayout.reset()
            holder.item.swipeLayout.isRightSwipeEnabled = false
        } else {
            holder.item.ivSelect.gone()
            holder.item.ivDownloadComplete.visible()
            holder.item.swipeLayout.reset()
            holder.item.swipeLayout.isRightSwipeEnabled = true
        }
        if (download.isContentSelected) {
            holder.item.ivSelect.setImageResource(R.drawable.vd_ellipse_tick)
        } else {
            holder.item.ivSelect.setImageResource(R.drawable.vd_ellipse_blank)
        }

        holder.item.llMain.setOnClickListener {
            if (viewModel.isEditEnable) {
                downloadList[position].isContentSelected =
                    downloadList[position].isContentSelected != true
                notifyItemChanged(position)
                downloadItemClickListener.onDownloadItemClick()
            } else {
                if (downloadList[position].availableEndDate?.isValidDate() == true) {
                    downloadItemClickListener.onContentView(download)
                }
            }
        }

        holder.item.llDeleteDownload.setOnClickListener {
            downloadItemClickListener.onSingleDownloadDelete(position)
        }

        if (downloadList[position].availableEndDate?.isValidDate() == true) {
            holder.item.videoDeletedView.gone()
        } else {
            holder.item.videoDeletedView.visible()
        }

        val contentFile: String? =  if (!download.contentFileForDownload.isNullOrEmpty()) {
            download.contentFileForDownload
        } else {
            download.contentFile
        }


        val handlerMainTimer = Handler(Looper.getMainLooper())
        val runnableMainTimer: Runnable = object : Runnable {
            override fun run() {
                val isDownloaded =
                    TaoCalligraphy.instance.getDownloadManager()?.currentDownloads?.find {
                        it.request.uri == linkUri(contentFile)
                    }
                if (holder.item.circleProgressDownload.tag == download.id) {
                    if ((isContentDownloaded(contentFile) == true) or (isDownloaded?.state == Download.STATE_COMPLETED)) {
                        if (!viewModel.isEditEnable) {
                            holder.item.ivDownloadComplete.visible()
                        }
                        holder.item.circleProgressDownload.gone()
                        handlerMainTimer.removeCallbacks(this)
                    } else if (isDownloaded != null) {
                        holder.item.ivDownloadComplete.gone()
                        holder.item.circleProgressDownload.visible()
                        holder.item.circleProgressDownload.progress =
                            isDownloaded.percentDownloaded.toInt()
                        handlerMainTimer.postDelayed(this, 2000)
                    } else {
                        if (!viewModel.isEditEnable) {
                            holder.item.ivDownloadComplete.visible()
                        }
                        holder.item.circleProgressDownload.gone()
                        handlerMainTimer.removeCallbacks(this)
                    }
                }
            }
        }

        handlerMainTimer.postDelayed(runnableMainTimer, 1000)

        handlerList.add(handlerMainTimer)
        runnableList.add(runnableMainTimer)
    }

    override fun getItemCount(): Int {
        return downloadList.size
    }

    class ViewHolder(view: ItemDownloadListBinding) : RecyclerView.ViewHolder(view.root) {
        val item = view
    }

    interface DownloadItemClickListener {
        fun onDownloadItemClick()
        fun onSingleDownloadDelete(adapterPosition: Int)
        fun onContentView(meditationContentResponse: MeditationContentResponse?)
    }

    fun linkUri(filePath: String?): Uri? {
        return Uri.parse(filePath)
    }

    fun isContentDownloaded(filePath: String?): Boolean? {
        return TaoCalligraphy.instance.getDownloadTracker()
            ?.isDownloaded(linkUri(filePath))
    }
}