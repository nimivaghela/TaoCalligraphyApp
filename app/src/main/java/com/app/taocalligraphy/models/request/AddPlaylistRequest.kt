package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class AddPlaylistRequest(
    @SerializedName("contentDetails")
    var contentDetails: ArrayList<ContentDetail> = ArrayList<ContentDetail>(),
    @SerializedName("languageId")
    var languageId: Int = -1,
    @SerializedName("title")
    var title: String = ""
) {
    data class ContentDetail(
        @SerializedName("contentId")
        var contentId: Int = -1,
        @SerializedName("contentOrder")
        var contentOrder: Int = -1
    )
}