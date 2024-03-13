package com.app.taocalligraphy.models.request


import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class UserInterestRequest(
    @SerializedName("deletedKeywordIds")
    var deletedKeywordIds: JsonArray = JsonArray(),
    @SerializedName("isViewInterest")
    var isViewInterest: Boolean=false,
    @SerializedName("keywordIds")
    var keywordIds: JsonArray = JsonArray()
)