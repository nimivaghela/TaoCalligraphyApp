package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class FetchHowToMeditateRequest(
    @SerializedName("current_page")
    var currentPage: Int? = 0,
    @SerializedName("per_page")
    var perPage: Int? = 0,
    @SerializedName("search")
    var search: String? = "",
    @SerializedName("sort")
    var sort: Sort? = null
) {
    data class Sort(
        @SerializedName("field")
        val `field`: String?,
        @SerializedName("order")
        val order: String?
    )
}
