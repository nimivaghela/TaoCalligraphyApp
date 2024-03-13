package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class FetchHomeDataRequest(
    @SerializedName("current_page")
    var current_page: Int = 0,
    @SerializedName("per_page")
    var per_page: Int = 0,
    @SerializedName("search")
    var search: ArrayList<SearchHomeDataModel> = arrayListOf(),
    @SerializedName("sort")
    var sort: SortHomeDataModel? = null
)

data class SortHomeDataModel(
    @SerializedName("field")
    var field: String = "",
    @SerializedName("order")
    var order: String = ""
)


data class SearchHomeDataModel(
    @SerializedName("field")
    var field: String = "",
    @SerializedName("keyword")
    var keyword: String = ""
)

