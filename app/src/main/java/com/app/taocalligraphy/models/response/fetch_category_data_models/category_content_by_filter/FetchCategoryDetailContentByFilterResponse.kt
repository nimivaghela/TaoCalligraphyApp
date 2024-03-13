package com.app.taocalligraphy.models.response.fetch_category_data_models.category_content_by_filter

import com.app.taocalligraphy.other.Constants
import com.google.gson.annotations.SerializedName

data class FetchCategoryDetailContentByFilterResponse(
    @SerializedName("guidedMeditationAudioDetail")
    val guidedMeditationAudioDetail: GuidedMeditationAudioDetail?,
    @SerializedName("musicDetail")
    val musicDetail: MusicDetail?,
    @SerializedName("videoDetail")
    val videoDetail: VideoDetail?
)

data class GuidedMeditationAudioDetail(
    @SerializedName("list")
    val list: ArrayList<ContentData?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)

data class MusicDetail(
    @SerializedName("list")
    val list: ArrayList<ContentData?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)

data class VideoDetail(
    @SerializedName("list")
    val list: ArrayList<ContentData?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
)

data class ContentData(
    @SerializedName("contentImage")
    val contentImage: String?,
    val thumbnailImage: String? = "",
    @SerializedName("contentName")
    val contentName: String?,
    @SerializedName("id")
    val id: Int?,
    @SerializedName("isFavorite")
    var isFavorite: Boolean?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("isSubscribed")
    var isSubscribed: Boolean?,
    @SerializedName("subscription")
    var subscription: Subscription?,
    @SerializedName("isLocked")
    var isLocked: Boolean?,
    @SerializedName("isPaidContent")
    var isPaidContent: Boolean?,
    @SerializedName("isPurchased")
    var isPurchased: Boolean?,
    @SerializedName("isFeatured")
    var isFeatured: Boolean?,
    @SerializedName("unlockDays")
    var unlockDays: Int? = null,

    ){
    data class Subscription(
        @SerializedName("isAccessible")
        var isAccessible: Boolean? = false,
        @SerializedName("badge")
        var badge: String? = "",
    )
}
