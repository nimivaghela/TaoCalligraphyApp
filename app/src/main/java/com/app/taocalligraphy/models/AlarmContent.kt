package com.app.taocalligraphy.models

import android.os.Parcelable
import com.app.taocalligraphy.models.response.alarm.AlarmResponse
import com.app.taocalligraphy.models.response.playList.PlaylistContentFilterApiResponse
import kotlinx.android.parcel.Parcelize

@Parcelize
class AlarmContent : Parcelable {
    var alarmContentId = ""
    var alarmContentTitle = ""
    var alarmContentImageUrl = ""
    var alarmContentFileUrl = ""

    fun setAlarmDataFromChooseContent(content: PlaylistContentFilterApiResponse.ContentList) {
        alarmContentId = content.id.toString()
        alarmContentTitle = content.contentName
        alarmContentImageUrl = content.thumbnailImage
        alarmContentFileUrl = if (!content.contentFileForDownload.isNullOrEmpty()) {
            content.contentFileForDownload
        } else {
            content.contentFile ?: ""
        }
    }

    fun setAlarmDataFromResponse(content: AlarmResponse) {
        alarmContentId = content.contentId ?: "0"
        alarmContentTitle = content.contentTitle ?: ""
        alarmContentImageUrl = content.thumbnailImage ?: ""
        alarmContentFileUrl = if (!content.contentFileForDownload.isNullOrEmpty()) {
            content.contentFileForDownload
        } else {
            content.contentFile ?: ""
        }
    }
}