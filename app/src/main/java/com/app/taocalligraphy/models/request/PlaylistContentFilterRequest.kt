package com.app.taocalligraphy.models.request


import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class PlaylistContentFilterRequest(
    @SerializedName("categoryId")
    var categoryId: Int = -1,
    @SerializedName("current_page")
    var currentPage: Int = -1,
    @SerializedName("per_page")
    var perPage: Int = -1,
    @SerializedName("search")
    var search: String="",
    @SerializedName("subCategoryIds")
    var subCategoryIds: JsonArray = JsonArray(),
    @SerializedName("tagsId")
    var tagsId: JsonArray = JsonArray(),
)