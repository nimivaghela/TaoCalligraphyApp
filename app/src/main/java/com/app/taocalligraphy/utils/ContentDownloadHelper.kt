package com.app.taocalligraphy.utils

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.webkit.MimeTypeMap
import com.app.taocalligraphy.R
import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseActivity
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.databinding.ViewToolbarBinding
import com.app.taocalligraphy.models.eventbus.MeditationContentDownloadListener
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse
import com.app.taocalligraphy.other.Constants.ERROR
import com.app.taocalligraphy.other.UserHolder
import com.app.taocalligraphy.utils.extensions.*
import com.google.android.exoplayer2.offline.Download
import org.greenrobot.eventbus.EventBus
import java.io.File

class ContentDownloadHelper : OnUserDownloadsListener {
    var activity: Activity? = null
    var toolbarBinding: ViewToolbarBinding? = null
    var meditationContentResponse: MeditationContentResponse? = null
    var downloadStep = 0
    var handlerMainTimer = Handler(Looper.getMainLooper())
    var viewModel: BaseViewModel = BaseViewModel()


    fun setView(activity: Activity, toolbar: ViewToolbarBinding) {
        this.activity = activity
        toolbarBinding = toolbar
        downloadStep = 0
        setDownloadClickListener()
    }

    fun setMeditationContentData(meditationContentResponse: MeditationContentResponse) {
        this.meditationContentResponse = meditationContentResponse
        setDownloadStateWiseImage()
    }

    private fun setDownloadClickListener() {
        toolbarBinding?.viewDownload?.setOnClickListener {
            if(!(UserHolder.EnumUserModulePermission.USE_DOWNLOAD_FUNCTION.permission?.canAccess ?: false))
                return@setOnClickListener

            if (activity?.isNetwork() == false) {
                return@setOnClickListener
            }

            meditationContentResponse?.id?.let {
                val data = viewModel.getMeditationContent(it)
                if (data == null) {
                    if (downloadStep == 0)
                    //viewModel.getUserDownloads(viewModel, this)

                        //if (viewModel.getTotalDownloads() < 10) {
                        setDownloadImage()
                        /*} else {
                            toolbarBinding?.ivDownloadToolbar?.context?.getString(
                                R.string.download_limit_message
                            )?.let { it1 ->
                                (activity as BaseActivity<*>).longToast(
                                    it1, ERROR
                                )
                            }
                        }*/
                    else {
                        setDownloadImage()
                    }
                }
            }
        }
    }

    private fun setDownloadImage() {
        meditationContentResponse?.let {
            val contentFile: String? = if (!it.contentFileForDownload.isNullOrEmpty()) {
                it.contentFileForDownload
            } else {
                it.contentFile
            }
            val isDownload = isContentDownloaded(contentFile)
            if ((isDownload == false) and (downloadStep == 0)) {
                downloadStep = 1
                toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_start_download)
                toolbarBinding?.tvContentFileSize?.visible()
                toolbarBinding?.tvContentFileSize?.text = it.contentFileSize.getContentFileSize()
                return
            } else if (isDownload == false) {
                if (downloadStep == 1) {
                    val renderersFactory = TaoCalligraphy.instance
                        .buildRenderersFactory()
                    TaoCalligraphy.instance.getDownloadTracker()
                        ?.downloadFile(
                            it.title,
                            linkUri(contentFile),
                            "",
                            renderersFactory,
                            viewModel
                        )
                    it.subtitleWithLanguages?.forEach { subtitleWithLanguage ->
                        TaoCalligraphy.instance.getDownloadTracker()
                            ?.downloadFile(
                                subtitleWithLanguage.languageName,
                                Uri.parse(subtitleWithLanguage.subTitleFile),
                                "",
                                renderersFactory,
                                viewModel
                            )
                    }
                    downloadStep = 2
                    toolbarBinding?.tvContentFileSize?.gone()
                    toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_stop_download)
                    handlerMainTimer.postDelayed(runnableMainTimer, 3000)
                    TaoCalligraphy.instance.currentDownloadItemList.add(
                        meditationContentResponse
                    )
                } else {
                    removeHandler()
                    TaoCalligraphy.instance.getDownloadTracker()
                        ?.removeDownload(linkUri(contentFile))
                    it.subtitleWithLanguages?.forEach { subtitleWithLanguage ->
                        TaoCalligraphy.instance.getDownloadTracker()
                            ?.removeDownload(linkUri(subtitleWithLanguage.subTitleFile))
                    }
                    setDefaultSet()
                }
                EventBus.getDefault().post(MeditationContentDownloadListener(downloadStep))
            } else {
                viewModel.addMeditationToStorage(it)
                toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_complete)
            }
        }
    }

    private fun setDefaultSet() {
        downloadStep = 0
        toolbarBinding?.circleProgressDownload?.gone()
        toolbarBinding?.tvContentFileSize?.gone()
        toolbarBinding?.circleProgressDownload?.progress = 0
        toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_icon)
    }

    private fun setDownloadStateWiseImage() {
        toolbarBinding?.tvContentFileSize?.gone()
        removeHandler()
        if (downloadStep == 3) {
            toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_complete)
            return
        }
        /*if (isContentDownloaded(meditationContentResponse?.contentFile) == true) {
            downloadStep = 3
            toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_complete)
        } else {*/
        meditationContentResponse?.id?.let {
            val data = viewModel.getMeditationContent(it)
            data?.let {
                downloadStep = 3
                toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_complete)
            } ?: kotlin.run {
                val contentFile: String? =
                    if (!meditationContentResponse?.contentFileForDownload.isNullOrEmpty()) {
                        meditationContentResponse?.contentFileForDownload
                    } else {
                        meditationContentResponse?.contentFile
                    }
                val download =
                    TaoCalligraphy.instance.getDownloadManager()?.currentDownloads?.find { download ->
                        download.request.uri == linkUri(contentFile)
                    }
                if (download == null) {
                    toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_icon)
                } else {
                    downloadStep = 2
                    toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_stop_download)
                    handlerMainTimer.postDelayed(runnableMainTimer, 1000)
                }
            }
        }
        //}
    }

    fun updateDownloadFromEventBus(status: Int) {
        if (downloadStep != status) {
            downloadStep = status
            startStopDownload()
        }
    }

    private fun startStopDownload() {
        removeHandler()
        if (downloadStep == 2) {
            toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_stop_download)
            handlerMainTimer.postDelayed(runnableMainTimer, 3000)
        } else {
            setDefaultSet()
        }
    }

    fun isContentDownloaded(filePath: String?): Boolean? {
        return TaoCalligraphy.instance.getDownloadTracker()
            ?.isDownloaded(linkUri(filePath))
    }

    fun linkUri(filePath: String?): Uri? {
        return Uri.parse(filePath)
    }

    private val runnableMainTimer: Runnable = object : Runnable {
        override fun run() {
            if ((activity?.isFinishing == false)) {
                val contentFile: String? =
                    if (!meditationContentResponse?.contentFileForDownload.isNullOrEmpty()) {
                        meditationContentResponse?.contentFileForDownload
                    } else {
                        meditationContentResponse?.contentFile
                    }
                val download =
                    TaoCalligraphy.instance.getDownloadManager()?.currentDownloads?.find {
                        it.request.uri == linkUri(contentFile)
                    }
                if ((isContentDownloaded(contentFile) == true) or (download?.state == Download.STATE_COMPLETED)) {
                    downloadStep = 3
                    toolbarBinding?.circleProgressDownload?.gone()
                    toolbarBinding?.circleProgressDownload?.progress = 0
                    toolbarBinding?.ivDownloadStatus?.setImageResource(R.drawable.vd_download_complete)
                } else {
                    if (download != null) {
                        downloadStep = 2
                        toolbarBinding?.circleProgressDownload?.visible()
                        toolbarBinding?.circleProgressDownload?.progress =
                            if (download.percentDownloaded == null) 0 else download.percentDownloaded.toInt()
                        handlerMainTimer.postDelayed(this, 2000)
                    } else {
                        setDefaultSet()
                        removeHandler()
                    }
                }
            }
        }
    }

    fun removeHandler() {
        handlerMainTimer.removeCallbacks(runnableMainTimer)
    }

    override fun onUserDownloadsCountListener(count: Int) {
        if (count < 10) {
            setDownloadImage()
        } else {
            toolbarBinding?.ivDownloadToolbar?.context?.getString(
                R.string.download_limit_message
            )?.let {
                (activity as BaseActivity<*>).longToast(
                    it, ERROR
                )
            }
        }
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        var extension = ""

        //Check uri format to avoid null
        extension = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //If scheme is a content
            val mime: MimeTypeMap = MimeTypeMap.getSingleton()
            mime.getExtensionFromMimeType(context.contentResolver.getType(uri)) ?: ""
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(uri.path?.let { File(it) }).toString())
        }
        return extension
    }
}

interface OnUserDownloadsListener {
    fun onUserDownloadsCountListener(count: Int)
}