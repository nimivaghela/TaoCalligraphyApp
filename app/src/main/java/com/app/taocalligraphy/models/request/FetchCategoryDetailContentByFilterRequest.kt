package com.app.taocalligraphy.models.request


import com.google.gson.annotations.SerializedName

data class FetchCategoryDetailContentByFilterRequest(
    @SerializedName("categoryId")
    var categoryId: Int? = 0,
    @SerializedName("contentType")
    var contentType: String? = "",
    @SerializedName("current_page")
    var currentPage: Int? = 0,
    @SerializedName("per_page")
    var perPage: Int? = 0,
    @SerializedName("search")
    var search: String? = "",
    @SerializedName("tagsId")
    var tagsId: ArrayList<Int?>? = arrayListOf()
)

data class PaginationRequest(
    @SerializedName("current_page")
    var currentPage: Int? = 0,
    @SerializedName("per_page")
    var perPage: Int? = 0,
    @SerializedName("search")
    var search: String? = ""
)