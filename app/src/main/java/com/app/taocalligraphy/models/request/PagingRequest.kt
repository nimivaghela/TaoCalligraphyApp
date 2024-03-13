package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class PagingRequest(
    @SerializedName("current_page")
    var currentPage: Int = -1,
    @SerializedName("per_page")
    var perPage: Int = -1,
    @SerializedName("search")
    var search: String = ""
)