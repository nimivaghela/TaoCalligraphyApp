package com.app.taocalligraphy.models.response.fetch_category_data_models.category_by_id

import com.google.gson.annotations.SerializedName

data class FetchCategoryDetailByIdResponse(
    @SerializedName("guidedMeditationAudioDetail")
    val guidedMeditationAudioDetail: GuidedMeditationAudioDetail?,
    @SerializedName("musicDetail")
    val musicDetail: MusicDetail?,
    @SerializedName("videoDetail")
    val videoDetail: VideoDetail?
)

data class GuidedMeditationAudioDetail(
    @SerializedName("contentList")
    val contentList: ArrayList<Content?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
) {
    data class Content(
        @SerializedName("contentImage")
        val contentImage: String?,
        val thumbnailImage: String? = "",
        @SerializedName("contentName")
        val contentName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("type")
        val type: String?
    )
}

data class MusicDetail(
    @SerializedName("contentList")
    val contentList: ArrayList<Content?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
) {
    data class Content(
        @SerializedName("contentImage")
        val contentImage: String?,
        val thumbnailImage: String? = "",
        @SerializedName("contentName")
        val contentName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("type")
        val type: String?
    )
}

data class VideoDetail(
    @SerializedName("contentList")
    val contentList: ArrayList<Content?>?,
    @SerializedName("totalRecords")
    val totalRecords: Int?
) {
    data class Content(
        @SerializedName("contentImage")
        val contentImage: String?,
        val thumbnailImage: String? = "",
        @SerializedName("contentName")
        val contentName: String?,
        @SerializedName("id")
        val id: Int?,
        @SerializedName("type")
        val type: String?
    )
}