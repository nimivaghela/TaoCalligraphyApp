package com.app.taocalligraphy.models.request

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class EditPlaylistRequest(
    @SerializedName("contentDetails")
    var contentDetails: ArrayList<AddPlaylistRequest.ContentDetail> = ArrayList<AddPlaylistRequest.ContentDetail>(),
    @SerializedName("deletedContentIds")
    var deletedContentIds: JsonArray = JsonArray(),
    @SerializedName("id")
    var id: Int = -1,
    @SerializedName("languageId")
    var languageId: Int = -1,
    @SerializedName("title")
    var title: String = ""
)