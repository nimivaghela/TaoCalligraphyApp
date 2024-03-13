package com.app.taocalligraphy.models.request

import com.google.gson.annotations.SerializedName

data class FetchCategoryTagsByIdRequest(
    @SerializedName("categoryIds")
    var categoryIds: ArrayList<Int>? = null
)