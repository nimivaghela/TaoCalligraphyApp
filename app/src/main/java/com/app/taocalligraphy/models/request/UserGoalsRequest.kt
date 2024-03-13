package com.app.taocalligraphy.models.request


import com.google.gson.JsonArray
import com.google.gson.annotations.SerializedName

data class UserGoalsRequest(
    @SerializedName("deletedKeywordIds")
    var deletedKeywordIds: JsonArray = JsonArray(),
    @SerializedName("keywordIds")
    var keywordIds:JsonArray = JsonArray(),
    @SerializedName("screen")
    var screen: Int=-1,
    @SerializedName("dailyMeditationTarget")
    var dailyMeditationTarget: Int=-1
)