package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class ProgramListRequest(
    @SerializedName("current_page")
    var currentPage: Int = 0,
    @SerializedName("per_page")
    var perPage: Int = 0,
    @SerializedName("search")
    var search: String = ""
)