package com.app.taocalligraphy.ui.downloads

import com.app.taocalligraphy.TaoCalligraphy
import com.app.taocalligraphy.base.BaseViewModel
import com.app.taocalligraphy.models.response.meditation_content.MeditationContentResponse

class DownloadViewModel : BaseViewModel() {

    var userDownloadsList: ArrayList<MeditationContentResponse> = ArrayList()
    var isEditEnable = false
    var selectedPosition = -1
    var sortBy = 0

    fun syncDownloads(onDownloadSyncListener: OnDownloadSyncListener) {
        TaoCalligraphy.instance.getDownloadTracker()?.removeAllDownload()
        userDownloadsList.reversed().forEach {
            downloadContent(it.id, this, onDownloadSyncListener)
        }
    }
}