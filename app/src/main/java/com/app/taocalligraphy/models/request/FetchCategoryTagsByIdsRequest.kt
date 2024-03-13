package com.app.taocalligraphy.models.request

import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class FetchCategoryTagsByIdsRequest(
    @SerializedName("categoryIds")
    var categoryIds: JsonArray = JsonArray()
)