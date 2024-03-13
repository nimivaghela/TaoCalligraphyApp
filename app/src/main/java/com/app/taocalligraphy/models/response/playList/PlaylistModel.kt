package com.app.taocalligraphy.models.response.playList


import com.google.gson.annotations.SerializedName

data class PlaylistModel(
    @SerializedName("contentDetails")
    var contentDetails: ArrayList<ContentDetail>,
    @SerializedName("languageId")
    var languageId: Int,
    @SerializedName("title")
    var title: String
) {
    data class ContentDetail(
        @SerializedName("contentId")
        var contentId: Int,
        @SerializedName("contentOrder")
        var contentOrder: Int,
        @SerializedName("type")
        var type: String,
        @SerializedName("contentName")
        var contentName: String
    )
}