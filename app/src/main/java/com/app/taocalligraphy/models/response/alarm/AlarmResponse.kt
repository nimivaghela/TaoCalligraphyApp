package com.app.taocalligraphy.models.response.alarm


import com.google.gson.annotations.SerializedName

data class AlarmResponse(
    @SerializedName("contentFile")
    val contentFile: String?,
    val contentFileForHls: String? = "",
    val contentFileForDownload: String? = "",
    @SerializedName("contentId")
    val contentId: String?,
    @SerializedName("contentImage")
    val contentImage: String?,
    val thumbnailImage: String? = "",
    @SerializedName("contentTitle")
    val contentTitle: String?,
    @SerializedName("contentType")
    val contentType: String?,
    @SerializedName("isWakeUpWithMeditation")
    val isWakeUpWithMeditation: Boolean?,
    @SerializedName("repeatDays")
    val repeatDays: List<Int>?,
    @SerializedName("time")
    val time: String?
)