package com.app.taocalligraphy.models.request

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

    data class ReferralsRequest(
        @SerializedName("current_page")
        var current_page: Int = -1,
        @SerializedName("per_page")
        var per_page: Int = -1,
        @SerializedName("search")
        var search: String="",
    )