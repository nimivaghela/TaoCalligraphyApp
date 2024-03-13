package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class SearchByAllDataRequest(
    @SerializedName("current_page")
    var current_page: Int = 0,
    @SerializedName("per_page")
    var per_page: Int = 0,
    @SerializedName("search")
    var search: String = "",
    @SerializedName("sort")
    var sort: SortDataModel? = null

) {
    data class SortDataModel(
        @SerializedName("field")
        var field: String = "",
        @SerializedName("order")
        var order: String = ""

    )
}


